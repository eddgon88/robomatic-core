package com.robomatic.core.v1.enums;

public enum RoleEnum {

    ADMIN("admin", 1),
    EXECUTOR("executor", 2),
    VIEWER("viewer", 3),
    ANALYST("analyst", 4);

    private final String value;
    private final Integer code;

    RoleEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static RoleEnum getMethodByCode(Integer code) {
        for(RoleEnum m : values()){
            if( m.code.equals(code)){
                return m;
            }
        }
        return null;
    }

}
