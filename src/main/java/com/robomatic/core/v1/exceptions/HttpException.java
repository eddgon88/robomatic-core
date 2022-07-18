package com.robomatic.core.v1.exceptions;

public class HttpException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Integer status;
    private final String code;

    public HttpException(final Integer status, final String code, final String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getCode() {
        return this.code;
    }

}
