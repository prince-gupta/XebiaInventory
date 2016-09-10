package com.xebia.dto;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Pgupta on 06-09-2016.
 */
public class AssetApprovalDTO {
    private BigInteger incidentId;
    private String assetType;
    private String modifiedBy;
    private String remark;
    private Date dateTillValid;
    private Date submittedDate;
    private String status;
    private String displayStatus;
    private String specificRequirement;
    private String raisedBy;
    private String employeeCode;
    private boolean isPending;
    private boolean showApproved;

    public BigInteger getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(BigInteger incidentId) {
        this.incidentId = incidentId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDateTillValid() {
        return dateTillValid;
    }

    public void setDateTillValid(Date dateTillValid) {
        this.dateTillValid = dateTillValid;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecificRequirement() {
        return specificRequirement;
    }

    public void setSpecificRequirement(String specificRequirement) {
        this.specificRequirement = specificRequirement;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean isPending) {
        this.isPending = isPending;
    }

    public boolean isShowApproved() {
        return showApproved;
    }

    public void setShowApproved(boolean showApproved) {
        this.showApproved = showApproved;
    }
}
