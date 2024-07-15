package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.models.AuthRequest;
import com.robomatic.core.v1.models.ConfirmUserResponse;
import com.robomatic.core.v1.models.SingUpRequest;

public interface AuthService {

    UserEntity login(AuthRequest authRequest);

    UserEntity singUp(SingUpRequest singUpRequest);

    ConfirmUserResponse confirmUser(String token);

}
