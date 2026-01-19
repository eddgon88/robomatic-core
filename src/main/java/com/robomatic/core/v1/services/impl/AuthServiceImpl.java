package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.clients.MailClient;
import com.robomatic.core.v1.dtos.mail.MailDto;
import com.robomatic.core.v1.entities.TokenEntity;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.enums.TokenStatusEnum;
import com.robomatic.core.v1.exceptions.BadRequestException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.BadRequestErrorCode;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.mappers.AuthMapper;
import com.robomatic.core.v1.models.AuthRequest;
import com.robomatic.core.v1.models.ConfirmUserResponse;
import com.robomatic.core.v1.models.ForgotPasswordRequest;
import com.robomatic.core.v1.models.ResetPasswordRequest;
import com.robomatic.core.v1.models.ResetPasswordResponse;
import com.robomatic.core.v1.models.SendMailRequest;
import com.robomatic.core.v1.models.SingUpRequest;
import com.robomatic.core.v1.repositories.TokenRepository;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.AuthService;
import com.robomatic.core.v1.services.PasswordEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final MyAuthenticationManager authenticationManager;

    public AuthServiceImpl(MyAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private MailDto mailDto;

    @Autowired
    private PasswordEncryptionService passwordEncryptionService;

    @Value("${mail.recoveryTemplateId}")
    private String recoveryTemplateId;

    @Override
    public UserEntity login(AuthRequest authRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.getEmail(), authRequest.getPass()
                        )
                );
        return userRepository.findByEmail(authenticate.getPrincipal().toString())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserEntity singUp(SingUpRequest singUpRequest) {

        AtomicReference<UserEntity> user = new AtomicReference<>();
        try {
            userRepository.findByEmail(singUpRequest.getEmail()).ifPresentOrElse(u -> {
                        log.error("An user alrady exist with this mail - {}", u.getEmail());
                        throw new BadRequestException(BadRequestErrorCode.E400002);
                    }, () -> {
                        user.set(userRepository.save(authMapper.crateUserEntity(singUpRequest)));

                        TokenEntity token = tokenRepository.save(authMapper.createTokenEntity(user.get().getId()));

                        Map<String, String> bodyDict = new HashMap<>();
                        bodyDict.put("confirm_link", mailDto.getEndpoint().getConfirm().replace("##token##", token.getToken()));

                        mailClient.sendMail(SendMailRequest.builder()
                                .email(Collections.singletonList(user.get().getEmail()))
                                .subject("Mail confirmation - Robomatic")
                                .executionId("singup")
                                .templateId(mailDto.getTemplateId())
                                .bodyDict(bodyDict)
                                .build());
                    }
            );
        } catch (Exception e) {
            log.error("Error singing up - {}", e.getMessage());
            throw e;
        }
        return user.get();
    }

    @Override
    public ConfirmUserResponse confirmUser(String token) {
        ConfirmUserResponse ret = ConfirmUserResponse.builder().build();
        tokenRepository.findByToken(token).ifPresentOrElse(t -> {
            UserEntity user = userRepository.findById(t.getUserId())
                    .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404008));
            ret.setMail(user.getEmail());
            user.setEnabled(true);
            userRepository.save(user);
            ret.setConfirm(true);
        }, () -> ret.setConfirm(false));
        return ret;
    }

    @Override
    public ResetPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Password recovery requested for email: {}", request.getEmail());
        
        AtomicReference<ResetPasswordResponse> response = new AtomicReference<>(
            ResetPasswordResponse.builder()
                .success(false)
                .message("Si el email existe, recibirás un correo con instrucciones")
                .build()
        );
        
        try {
            userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                // Crear token de recuperación (expira en 1 hora)
                TokenEntity token = authMapper.createPasswordResetToken(user.getId());
                tokenRepository.save(token);
                
                // Preparar y enviar email
                Map<String, String> bodyDict = new HashMap<>();
                bodyDict.put("change_pass", mailDto.getEndpoint().getResetPassword().replace("##token##", token.getToken()));
                
                mailClient.sendMail(SendMailRequest.builder()
                        .email(Collections.singletonList(user.getEmail()))
                        .subject("Password Recovery - Robomatic")
                        .executionId("password-recovery")
                        .templateId(recoveryTemplateId)
                        .bodyDict(bodyDict)
                        .build());
                
                log.info("Password recovery email sent to: {}", maskEmail(user.getEmail()));
                response.set(ResetPasswordResponse.builder()
                        .success(true)
                        .email(maskEmail(user.getEmail()))
                        .message("Email de recuperación enviado")
                        .build());
            });
        } catch (Exception e) {
            log.error("Error in password recovery: {}", e.getMessage());
            // No revelamos si el email existe o no por seguridad
        }
        
        return response.get();
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        log.info("Password reset attempt with token");
        
        return tokenRepository.findByToken(request.getToken())
                .map(token -> {
                    // Verificar que el token no esté expirado
                    if (token.getExpirationDate().before(new Date())) {
                        log.warn("Token expired");
                        return ResetPasswordResponse.builder()
                                .success(false)
                                .message("El link de recuperación ha expirado")
                                .build();
                    }
                    
                    // Verificar que el token no haya sido usado
                    if (!TokenStatusEnum.OPEN.getCode().equals(token.getStatus())) {
                        log.warn("Token already used");
                        return ResetPasswordResponse.builder()
                                .success(false)
                                .message("El link de recuperación ya fue utilizado")
                                .build();
                    }
                    
                    // Buscar usuario y actualizar contraseña
                    UserEntity user = userRepository.findById(token.getUserId())
                            .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404008));
                    
                    user.setEncryptedPass(passwordEncryptionService.encryptPassword(request.getNewPassword()));
                    userRepository.save(user);
                    
                    // Marcar token como usado
                    token.setStatus(TokenStatusEnum.CONSUMED.getCode());
                    tokenRepository.save(token);
                    
                    log.info("Password successfully reset for user: {}", maskEmail(user.getEmail()));
                    return ResetPasswordResponse.builder()
                            .success(true)
                            .email(maskEmail(user.getEmail()))
                            .message("Contraseña actualizada exitosamente")
                            .build();
                })
                .orElse(ResetPasswordResponse.builder()
                        .success(false)
                        .message("Token de recuperación inválido")
                        .build());
    }

    @Override
    public ResetPasswordResponse validateResetToken(String token) {
        log.info("Validating reset token");
        
        return tokenRepository.findByToken(token)
                .map(t -> {
                    // Verificar expiración
                    if (t.getExpirationDate().before(new Date())) {
                        return ResetPasswordResponse.builder()
                                .success(false)
                                .message("El link de recuperación ha expirado")
                                .build();
                    }
                    
                    // Verificar estado
                    if (!TokenStatusEnum.OPEN.getCode().equals(t.getStatus())) {
                        return ResetPasswordResponse.builder()
                                .success(false)
                                .message("El link de recuperación ya fue utilizado")
                                .build();
                    }
                    
                    // Token válido
                    UserEntity user = userRepository.findById(t.getUserId())
                            .orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404008));
                    
                    return ResetPasswordResponse.builder()
                            .success(true)
                            .email(maskEmail(user.getEmail()))
                            .message("Token válido")
                            .build();
                })
                .orElse(ResetPasswordResponse.builder()
                        .success(false)
                        .message("Token de recuperación inválido")
                        .build());
    }

    /**
     * Oculta parte del email por seguridad
     * ejemplo@domain.com -> ej***@domain.com
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) return name.charAt(0) + "***@" + domain;
        return name.substring(0, 2) + "***@" + domain;
    }
}
