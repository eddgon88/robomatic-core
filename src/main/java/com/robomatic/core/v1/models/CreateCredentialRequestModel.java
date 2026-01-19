package com.robomatic.core.v1.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCredentialRequestModel {

    private Integer testId;
    private Integer credentialTypeId;
    private String name;
    private String value;  // Valor en texto plano (se encriptará en el backend)
    private String fileName;  // Nombre del archivo para certificados
    private String fileContent;  // Contenido del archivo en Base64 para certificados

}


