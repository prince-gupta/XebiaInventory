package com.xebia.enums;

/**
 * Created by Pgupta on 20-08-2016.
 */
public enum ExcelMappingEnum {
    SNO("SNO."),
    DOI("Date Of Issue"),
    HN("Host Name"),
    ASSET_NAME("Asset Name"),
    EMPLOYEE_NAME("Employee Name"),
    DEPART("Department"),
    ASNO("Serial Number"),
    OS("Operating System"),
    MSOFFICE("MS Office"),
    HDD("HDD"),
    RAM("RAM"),
    PROCESSOR("Processor"),
    WED("Warranty End Date"),
    LB("Laptop Bag"),
    MOUSE("Mouse"),
    SPK("Speaker"),
    ARD("Asset Return Date"),
    HP("Head Phone"),
    SOU("Status Of User"),
    MOBILE("Mobile"),
    ATO("Accessories Taken Others");


    private String displayName;

    ExcelMappingEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static ExcelMappingEnum getByDisplayName(String displayName){
        for(ExcelMappingEnum excelMapping : ExcelMappingEnum.values()){
            if(excelMapping.displayName.equals(displayName))
                return excelMapping;
        }
        return null;
    }
}
