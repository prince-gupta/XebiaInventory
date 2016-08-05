package com.xebia.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Pgupta on 02-08-2016.
 */
public enum MailStatus {
    SENT("SENT",1),
    PENDING("PENDING",2),
    NOT_SENT("NOT_SENT",3),
    RETRYING_FAILED("RETRYING_FAILED",4),
    FAILED("FAILED",5),
    INVALID_EMAIL_ADDRESS("INVALID_EMAIL_ADDRESS",6);

    private String value;
    private int code;

    MailStatus(String value, int code){
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static MailStatus resovleStatus(String message){
        if(StringUtils.contains(message,"Invalid Addresses"))
            return INVALID_EMAIL_ADDRESS;
        return NOT_SENT;
    }
}
