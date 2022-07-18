package com.robomatic.core.v1.exceptions.messages;

public enum BadGatewayErrorCode {

    E502002("502002", "There's no transference bank account information.");

    private String code;
    private String message;

    BadGatewayErrorCode(final String code, final String message) {
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
