package com.xebia.enums;

/**
 * Created by Pgupta on 26-08-2016.
 */
public enum UserRoleEnum {
    IT("IT", "IT"),
    ADMIN("ADMIN", "Administrator"),
    EMP("EMP", "Employee");

    private String displayValue;
    private String dbValue;

    UserRoleEnum(String dbValue, String displayValue) {
        this.dbValue = dbValue;
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public void setDbValue(String dbValue) {
        this.dbValue = dbValue;
    }

    public static UserRoleEnum resolveByDBValue(String dbValue){
        for(UserRoleEnum roleEnum : UserRoleEnum.values()){
            if(roleEnum.dbValue.equals(dbValue)){
                return roleEnum;
            }
        }
        return null;
    }
}
