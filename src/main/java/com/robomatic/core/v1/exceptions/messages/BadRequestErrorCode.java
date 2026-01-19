package com.robomatic.core.v1.exceptions.messages;

public enum BadRequestErrorCode {

    E400001("400001", "Invalid permission type."),
    E400002("400002", "Conflicts creating folder."),
    E400003("400003", "Conflicts creating order."),
    E400004("400004", "Merchant-method customs values not found."),
    E400005("400005", "Couldn't create order custom values properly."),
    E400006("400006", "Payment method list invalid. One of those must be unique."),
    E400007("400007", "Payments methods unknown."),
    E400008("400008", "Custom list with duplicate keys."),
    E400009("400009", "customs list with missing key for at least one method."),
    E400010("400010", "A custom key with malformed value."),
    E400011("400011", "Payment details list with missing key."),
    E400012("400012", "This order is not in pending status."),
    E400013("400013", "Merchant does not have payment methods configured."),
    E400014("400014", "Conflicts creating transaction."),
    E400015("400015", "The order has not been paid, cannot be reversed."),
    E400016("400016", "This order does not allow any more refunds."),
    E400017("400017", "Cannot reverse a register order"),
    E400018("400018", "The tarjetas_card_type_allowed custom is invalid."),
    E400019("400019", "The tarjetas_quotas_allowed custom is invalid."),
    E400020("400020", "Rut is missing"),
    E400021("400021", "Type is missing"),
    E400022("400022", "statusEnrolledMastercardConnect is missing"),
    E400023("400023", "A schedule already exists for this test."),
    E400024("400024", "Invalid trigger type. Must be: cron, interval, or date."),
    E400025("400025", "Invalid schedule expression."),
    E400026("400026", "You don't have permission to manage schedules for this test.");

    private final String code;
    private final String message;

    BadRequestErrorCode(final String code, final String message) {
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
