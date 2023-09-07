package com.robomatic.core.v1.exceptions.messages;

public enum NotFoundErrorCode {

    E404001("404001", "Test Case not found."),
    E404002("404002", "Test not found."),
    E404003("404003", "Test Execution not found."),
    E404005("404005", "status value not found."),
    E404006("404006", "Null message"),
    E404007("404007", "folder not found."),
    E404008("404008", "Method not found."),
    E404009("404009", "Transaction status not found."),
    E404010("404010", "Transaction not found."),
    E404011("404011", "No transaction was found");

    private String code;
    private String message;

    NotFoundErrorCode(final String code, final String message) {
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
