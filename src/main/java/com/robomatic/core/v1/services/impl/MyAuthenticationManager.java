package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.repositories.UserRepository;
import com.robomatic.core.v1.services.PasswordEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyAuthenticationManager implements AuthenticationManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncryptionService passwordEncryptionService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String providedPassword = authentication.getCredentials().toString();
        boolean isAuthenticated = false;

        // Primero intentar con contraseña encriptada (nuevos usuarios)
        if (user.getEncryptedPass() != null && !user.getEncryptedPass().isEmpty()) {
            isAuthenticated = passwordEncryptionService.verifyPassword(providedPassword, user.getEncryptedPass());
        } 
        // Fallback a contraseña legacy (usuarios existentes)
        else if (user.getPass() != null && !user.getPass().isEmpty()) {
            isAuthenticated = providedPassword.equals(user.getPass());
        }

        if (!isAuthenticated) {
            throw new BadCredentialsException("NOT AUTHORIZED");
        }

        return authentication;
    }
}
