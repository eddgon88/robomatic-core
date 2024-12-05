package com.robomatic.core.v1.controllers;

import com.robomatic.core.v1.commons.FunctionCaller;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.models.AuthRequest;
import com.robomatic.core.v1.models.SingUpRequest;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.AuthService;
import com.robomatic.core.v1.utils.JwtUtil;
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
@CrossOrigin(origins = {"http://localhost:4200","http://robomatic.cloud","https://robomatic.cloud"})
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

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthRequest request) {
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

}
