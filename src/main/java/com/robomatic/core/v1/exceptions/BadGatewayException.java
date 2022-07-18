package com.robomatic.core.v1.exceptions;

import com.robomatic.core.v1.exceptions.messages.BadGatewayErrorCode;

public class BadGatewayException extends HttpException {
    private static final long serialVersionUID = 1L;

    public BadGatewayException(final String code, final String message) {
        super(502, code, message);
    }

    public BadGatewayException(final BadGatewayErrorCode badGatewayErrorCode) {
        this(badGatewayErrorCode.getCode(), badGatewayErrorCode.getMessage());
    }

    public BadGatewayException(final BadGatewayErrorCode badGatewayErrorCode, final String customMessage) {
        this(badGatewayErrorCode.getCode(), customMessage);
    }
}
