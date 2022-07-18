package com.robomatic.core.v1.models;

import com.robomatic.core.v1.exceptions.messages.InternalErrorCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorGateway {
    private String code;
    private String message;

    public ErrorGateway(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorGateway(final InternalErrorCode code) {
        this(code.getCode(), code.getMessage());
    }
}
