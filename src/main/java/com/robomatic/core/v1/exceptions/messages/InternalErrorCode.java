package com.robomatic.core.v1.exceptions.messages;

public enum InternalErrorCode {

    E500000("500000", "Unexpected error."),
    E500001("500001", "Internal error."),
    E500002("500002", "Couldn't create test case."),
    E500003("500003", "Couldn't Update test case."),
    E500004("500004", "Couldn't Update test."),
    E500005("500005", "Couldn't Create test."),
    E500006("500006", "Couldn't save method recurrencia"),
    E500007("500007", "Couldn't save method sodexo"),
    E500008("500008", "Couldn't save method efectivo_api"),
    E500009("500009", "Couldn't find merchants account update"),
    E500010("500010", "The validation webhook couldn't be notified."),
    E500011("500011", "Couldn't save method"),
    E500012("500012", "Couldn't update merchant"),
    E500013("500013", "Couldn't create merchant");

    private final String code;
    private final String message;

    InternalErrorCode(final String code, final String message) {
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
