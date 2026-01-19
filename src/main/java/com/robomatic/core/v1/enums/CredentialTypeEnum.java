package com.robomatic.core.v1.enums;

import lombok.Getter;

@Getter
public enum CredentialTypeEnum {

    PASSWORD(1, "PASSWORD"),
    CERTIFICATE(2, "CERTIFICATE");

    private final Integer code;
    private final String name;

    CredentialTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CredentialTypeEnum fromCode(Integer code) {
        for (CredentialTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid credential type code: " + code);
    }

}


