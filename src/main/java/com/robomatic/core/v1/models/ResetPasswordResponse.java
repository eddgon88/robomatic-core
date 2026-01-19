package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response de la operación de reset de contraseña
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordResponse {

    /**
     * Indica si el reset fue exitoso
     */
    private Boolean success;

    /**
     * Email del usuario (parcialmente oculto)
     */
    private String email;

    /**
     * Mensaje descriptivo
     */
    private String message;

}

