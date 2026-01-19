package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para establecer una nueva contraseña
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    /**
     * Token de recuperación enviado por email
     */
    private String token;

    /**
     * Nueva contraseña del usuario
     */
    private String newPassword;

}

