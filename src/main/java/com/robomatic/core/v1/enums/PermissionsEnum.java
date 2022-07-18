package com.robomatic.core.v1.enums;

public enum PermissionsEnum {

    OWNER("owner", 1),
    EDIT("edit", 2),
    EXECUTE("execute", 3),
    VIEW("view", 4);

    private final String value;
    private final Integer code;

    PermissionsEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static PermissionsEnum getMethodByCode(Integer code) {
        for(PermissionsEnum m : values()){
            if( m.code.equals(code)){
                return m;
            }
        }
        return null;
    }

}
