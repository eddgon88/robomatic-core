package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;
import com.robomatic.core.v1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public Optional<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        // Replace this with your own logic to fetch user details from the database
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404008));
        return Optional.of(new User(userEntity.getEmail(),
                "$2a$10$Bk16tW8XzkR8FzEc4FY6We.DJhBRyHlBccJv.s04FgBL.hJmL8qXG",
                new ArrayList<>())); // password is "password"
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException(NotFoundErrorCode.E404008));
        return new User(userEntity.getEmail(),
                "$2a$10$Bk16tW8XzkR8FzEc4FY6We.DJhBRyHlBccJv.s04FgBL.hJmL8qXG",
                new ArrayList<>()); // password is "password"
    }
}
