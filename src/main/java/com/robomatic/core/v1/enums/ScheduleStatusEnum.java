package com.robomatic.core.v1.enums;

public enum ScheduleStatusEnum {

    ACTIVE("active", 1),
    PAUSED("paused", 2),
    DELETED("deleted", 3);

    private final String value;
    private final Integer code;

    ScheduleStatusEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static ScheduleStatusEnum getByCode(Integer code) {
        for (ScheduleStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static ScheduleStatusEnum getByValue(String value) {
        for (ScheduleStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}


