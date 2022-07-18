package com.robomatic.core.v1.exceptions;

import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;

public class NotFoundException extends HttpException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(final String code, final String message) {
        super(404, code, message);
    }

    public NotFoundException(final NotFoundErrorCode notFoundErrorCode) {
        this(notFoundErrorCode.getCode(), notFoundErrorCode.getMessage());
    }

    public NotFoundException(final NotFoundErrorCode notFoundErrorCode, final String customMessage) {
        this(notFoundErrorCode.getCode(), customMessage);
    }

}
