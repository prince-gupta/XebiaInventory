package com.xebia.enums;

/**
 * Created by Pgupta on 06-09-2016.
 */
public enum ApprovalStateEnum {
    SENT("Sent", "SENT"),
    PENDING("Pending", "PENDING"),
    APPROVED("Approved", "APPROVED"),
    NOT_APPROVED("Not Approved", "NOT_APPROVED"),
    ATTENTION("Attention Required", "ATTENTION");

    private String displayName ;
    private String dbName;

    ApprovalStateEnum(String displayName, String dbName) {
        this.displayName = displayName;
        this.dbName = dbName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public static ApprovalStateEnum getByDBName(String dbName){
        for(ApprovalStateEnum approvalStateEnum : ApprovalStateEnum.values()){
            if(approvalStateEnum.dbName.equals(dbName))
                return approvalStateEnum;
        }
        return null;
    }
}
