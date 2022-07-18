package com.robomatic.core.v1.enums;

import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;

public enum StatusEnum {

    RUNNING("running", 1),
    SUCCESS("success", 2),
    FAILED("failed", 3),
    STOPPED("stopped", 4);

    private final String value;
    private final Integer code;

    StatusEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static StatusEnum getMethodByValue(String value) {
        for(StatusEnum m : values()){
            if( m.value.equals(value)){
                return m;
            }
        }
        throw new NotFoundException(NotFoundErrorCode.E404005);
    }

}
