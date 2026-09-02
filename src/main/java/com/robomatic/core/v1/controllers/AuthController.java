package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.models.AuthRequest;
import com.robomatic.core.v1.models.ForgotPasswordRequest;
import com.robomatic.core.v1.models.ResetPasswordRequest;
import com.robomatic.core.v1.models.SingUpRequest;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.AuthService;
import com.robomatic.core.v1.services.RateLimitService;
import com.robomatic.core.v1.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

@RestController
@RequestMapping("/core/v1/auth")
@Slf4j
public class AuthController {

    private final JwtUtil jwtTokenUtil;

    public AuthController(JwtUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private FunctionCaller functionCaller;

    @Autowired
    private RateLimitService rateLimitService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthRequest request, HttpServletRequest httpRequest) {
        // Obtener IP del cliente
        String clientIp = getClientIp(httpRequest);

        // Verificar rate limiting
        if (!rateLimitService.isAllowed(clientIp)) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Demasiados intentos de login. Intenta más tarde."));
        }

        try {
            Map<String, String> resp = new HashMap<>();

            UserEntity user = authService.login(request);

            resp.put("email", user.getEmail());
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateToken(user));
            headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resp);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/singup")
    public ResponseEntity<Object> singUp(@RequestBody @Valid SingUpRequest request) {
        try {
            Map<String, String> resp = new HashMap<>();

            UserEntity user = authService.singUp(request);

            resp.put("email", user.getEmail());
            return ResponseEntity.ok()
                    .body(resp);
        } catch (Exception ex) {
            log.error("Exception creating a new user {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/confirm/{token}")
    public ResponseEntity<Object> confirmUser(@PathVariable("token") String token) {
        UnaryOperator<Object> function = req -> authService.confirmUser((String) req);
        return functionCaller.callFunction(token, function, HttpStatus.OK);
    }

    /**
     * Solicita recuperación de contraseña - envía email con link
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        log.info("Password recovery requested for: {}", request.getEmail());
        UnaryOperator<Object> function = req -> authService.forgotPassword((ForgotPasswordRequest) req);
        return functionCaller.callFunction(request, function, HttpStatus.OK);
    }

    /**
     * Valida si el token de recuperación es válido
     */
    @GetMapping("/validate-reset-token/{token}")
    public ResponseEntity<Object> validateResetToken(@PathVariable("token") String token) {
        log.info("Validating reset token");
        UnaryOperator<Object> function = req -> authService.validateResetToken((String) req);
        return functionCaller.callFunction(token, function, HttpStatus.OK);
    }

    /**
     * Establece la nueva contraseña usando el token de recuperación
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("Password reset attempt");
        UnaryOperator<Object> function = req -> authService.resetPassword((ResetPasswordRequest) req);
        return functionCaller.callFunction(request, function, HttpStatus.OK);
    }

    /**
     * Obtiene la IP del cliente desde la solicitud HTTP
     */
    private String getClientIp(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", 
                           "HTTP_X_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_CLIENT_IP"};

        for (String header : headers) {
            String value = request.getHeader(header);
            if (value != null && !value.isEmpty() && !"unknown".equalsIgnoreCase(value)) {
                return value.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

}
