package com.robomatic.core.v1.enums;

public enum TestCaseEnum {

    DEFAULT("default", 1),
    SCHEDULED("scheduled", 2),
    SEQUENTIAL("sequential", 3);

    private final String value;
    private final Integer code;

    TestCaseEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static TestCaseEnum getMethodByCode(Integer code) {
        for(TestCaseEnum m : values()){
            if( m.code.equals(code)){
                return m;
            }
        }
        return null;
    }
}
