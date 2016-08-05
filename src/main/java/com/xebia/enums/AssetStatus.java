package com.xebia.enums;

/**
 * Created by Pgupta on 25-07-2016.
 */
public enum AssetStatus {
    ISSUED("ISSUED"),
    RETURNED("RETURNED"),
    NOT_ISSUED("NOT_ISSUED"),
    EXPIRED("EXPIRED");

    private String value;

    AssetStatus(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
