package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.mappers.AuthMapper;
import com.robomatic.core.v1.models.AuthRequest;
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

        try {
            UserEntity user = userRepository.save(authMapper.crateUserEntity(singUpRequest));

            tokenRepository.save(authMapper.createTokenEntity(user.getId()));

            return user;
        } catch (Exception e) {
            log.error("Error singing up - {}", e.getMessage());
            throw e;
        }
    }
}
