package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialModel {

    private Long id;
    private String credentialId;
    private Integer testId;
    private Integer credentialTypeId;
    private String credentialTypeName;
    private String name;
    private String fileName;
    // Note: No incluimos el valor encriptado ni desencriptado por seguridad
    private boolean hasValue;

}


