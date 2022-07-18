package com.robomatic.core.v1.enums;

public enum ActionEnum {

    CREATE("create", 1),
    UPDATE("update", 2),
    DELETE("delete", 3),
    EXECUTE("execute", 4),
    EXECUTE_PERMISSION("give execute permissions", 5),
    VIEW_PERMISSION("give viewer permissions", 6),
    EDIT_PERMISSION("give editor permissions", 7);

    private final String value;
    private final Integer code;

    ActionEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static ActionEnum getMethodByCode(Integer code) {
        for(ActionEnum m : values()){
            if( m.code.equals(code)){
                return m;
            }
        }
        return null;
    }
}
