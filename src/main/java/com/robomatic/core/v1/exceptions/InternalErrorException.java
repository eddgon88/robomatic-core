package com.robomatic.core.v1.exceptions;


import com.robomatic.core.v1.exceptions.messages.InternalErrorCode;

public class InternalErrorException extends HttpException {
    private static final long serialVersionUID = 1L;

    public InternalErrorException(final String code, final String message) {
        super(500, code, message);
    }

    public InternalErrorException(final InternalErrorCode internalErrorCode) {
        this(internalErrorCode.getCode(), internalErrorCode.getMessage());
    }

    public InternalErrorException(final InternalErrorCode internalErrorCode, final String customMessage) {
        this(internalErrorCode.getCode(), customMessage);
    }
}
