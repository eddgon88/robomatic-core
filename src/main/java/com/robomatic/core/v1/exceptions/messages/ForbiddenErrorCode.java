package com.robomatic.core.v1.exceptions.messages;

public enum ForbiddenErrorCode {

    E403001("403001", "The apikey is not valid."),
    E403002("403002", "Insufficient permissions to perform this action.");

    private String code;
    private String message;

    ForbiddenErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
