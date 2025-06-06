package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.clients.MailClient;
import com.robomatic.core.v1.dtos.mail.MailDto;
import com.robomatic.core.v1.entities.TokenEntity;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.exceptions.BadRequestException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.BadRequestErrorCode;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.mappers.AuthMapper;
import com.robomatic.core.v1.models.AuthRequest;
import com.robomatic.core.v1.models.ConfirmUserResponse;
import com.robomatic.core.v1.models.SendMailRequest;
import com.robomatic.core.v1.models.SingUpRequest;
import com.robomatic.core.v1.repositories.TokenRepository;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
}
