package com.robomatic.core.v1.services;

import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.models.AuthRequest;
import com.robomatic.core.v1.models.ConfirmUserResponse;
import com.robomatic.core.v1.models.ForgotPasswordRequest;
import com.robomatic.core.v1.models.ResetPasswordRequest;
import com.robomatic.core.v1.models.ResetPasswordResponse;
import com.robomatic.core.v1.models.SingUpRequest;

public interface AuthService {

    UserEntity login(AuthRequest authRequest);

    UserEntity singUp(SingUpRequest singUpRequest);

    ConfirmUserResponse confirmUser(String token);

    /**
     * Solicita recuperación de contraseña, envía email con link de recuperación
     * @param request contiene el email del usuario
     * @return response con resultado de la operación
     */
    ResetPasswordResponse forgotPassword(ForgotPasswordRequest request);

    /**
     * Valida el token y establece la nueva contraseña
     * @param request contiene el token y la nueva contraseña
     * @return response con resultado de la operación
     */
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);

    /**
     * Valida si un token de recuperación es válido
     * @param token el token a validar
     * @return response con el resultado
     */
    ResetPasswordResponse validateResetToken(String token);

}
