package com.robomatic.core.v1.enums;

public enum TriggerTypeEnum {

    CRON("cron"),
    INTERVAL("interval"),
    DATE("date");

    private final String value;

    TriggerTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TriggerTypeEnum getByValue(String value) {
        for (TriggerTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isValid(String value) {
        return getByValue(value) != null;
    }
}


