package com.xebia.dto;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Pgupta on 11-08-2016.
 */
public class AssetHistoryDTO {

    private String assetName;

    private String serialNumber;

    private String approverFirstName;

    private String approverLastName;

    private String updaterFirstName;

    private String updaterLastName;

    private Date dateOfIssue;

    private Date dateTillValid;

    private Date returnedDate;

    private String status;

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getApproverFirstName() {
        return approverFirstName;
    }

    public void setApproverFirstName(String approverFirstName) {
        this.approverFirstName = approverFirstName;
    }

    public String getApproverLastName() {
        return approverLastName;
    }

    public void setApproverLastName(String approverLastName) {
        this.approverLastName = approverLastName;
    }

    public String getUpdaterFirstName() {
        return updaterFirstName;
    }

    public void setUpdaterFirstName(String updaterFirstName) {
        this.updaterFirstName = updaterFirstName;
    }

    public String getUpdaterLastName() {
        return updaterLastName;
    }

    public void setUpdaterLastName(String updaterLastName) {
        this.updaterLastName = updaterLastName;
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

    public Date getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(Date returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
