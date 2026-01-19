package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCredentialRequestModel {

    private Long id;
    private String credentialId;
    private String name;
    private String value;  // Nuevo valor (opcional, si no se envía no se actualiza)
    private String fileName;  // Nuevo nombre del archivo (opcional)
    private String fileContent;  // Nuevo contenido del archivo en Base64 (opcional)

}


