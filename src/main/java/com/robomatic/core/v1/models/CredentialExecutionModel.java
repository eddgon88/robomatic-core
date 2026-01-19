package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo para enviar credenciales al executor.
 * Contiene el valor encriptado que será desencriptado en el executor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialExecutionModel {

    private String name;
    private Integer credentialTypeId;
    private String encryptedValue;  // Para passwords
    private String filePath;  // Para certificados

}


