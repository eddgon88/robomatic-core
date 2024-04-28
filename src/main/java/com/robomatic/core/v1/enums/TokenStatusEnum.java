package com.robomatic.core.v1.enums;

import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode;

public enum TokenStatusEnum {

    OPEN("open", 1),
    CONSUMED("consumed", 2),
    EXPIRED("expired", 3),
    CANCELLED("cancelled", 4);

    private final String value;
    private final Integer code;

    TokenStatusEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    /*public static TokenStatusEnum getMethodByValue(String value) {
        for(TokenStatusEnum m : values()){
            if( m.value.equals(value)){
                return m;
            }
        }
        throw new NotFoundException(NotFoundErrorCode.E404005);
    }

    public static TokenStatusEnum getStatusByCode(Integer code) {
        for(TokenStatusEnum m : values()){
            if( m.code.equals(code)){
                return m;
            }
        }
        throw new NotFoundException(NotFoundErrorCode.E404005);
    }*/

}
