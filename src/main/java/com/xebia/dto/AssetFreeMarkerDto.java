package com.xebia.dto;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Pgupta on 24-07-2016.
 */
public class AssetFreeMarkerDto {

    private String name;

    private String serialNumber;

    private Date dateOfIssue;

    private Date dateTillValid;

    private String approverName;

    private String issuedByName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public Date getDateTillValid() {
        return dateTillValid;
    }

    public void setDateTillValid(Date dateTillValid) {
        this.dateTillValid = dateTillValid;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getIssuedByName() {
        return issuedByName;
    }

    public void setIssuedByName(String issuedByName) {
        this.issuedByName = issuedByName;
    }
}

