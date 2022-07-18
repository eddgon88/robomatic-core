package com.robomatic.core.v1.exceptions;


import com.robomatic.core.v1.exceptions.messages.BadRequestErrorCode;

public class BadRequestException extends HttpException {

    private static final long serialVersionUID = 1L;

    public BadRequestException(final String code, final String message) {
        super(400, code, message);
    }

    public BadRequestException(final BadRequestErrorCode badRequestErrorCode) {
        this(badRequestErrorCode.getCode(), badRequestErrorCode.getMessage());
    }

    public BadRequestException(final BadRequestErrorCode badRequestErrorCode, final String customMessage) {
        this(badRequestErrorCode.getCode(), customMessage);
    }
}
