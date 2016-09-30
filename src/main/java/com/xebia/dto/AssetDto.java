package com.xebia.dto;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Pgupta on 24-07-2016.
 */
public class AssetDto {

    private BigInteger assetId;

    private Date dateOfPurchase;

    private String name;

    private String serialNumber;

    private BigInteger employee;

    private BigInteger approvedBy;

    private BigInteger assetManufacturer;

    private BigInteger assetType;

    private String assetTypeName;

    private String assetManufacturerName;

    private Date dateOfIssue;

    private Date dateTillValid;

    private Date returnedDate;

    private String status;

    private BigInteger hardwareConfiguration;

    private String employeeName;

    private String userName;

    private int offset;

    private int limit;

    public BigInteger getAssetId() {
        return assetId;
    }

    public void setAssetId(BigInteger assetId) {
        this.assetId = assetId;
    }

    public Date getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

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

    public BigInteger getEmployee() {
        return employee;
    }

    public void setEmployee(BigInteger employee) {
        this.employee = employee;
    }

    public BigInteger getAssetManufacturer() {
        return assetManufacturer;
    }

    public void setAssetManufacturer(BigInteger assetManufacturer) {
        this.assetManufacturer = assetManufacturer;
    }

    public BigInteger getAssetType() {
        return assetType;
    }

    public void setAssetType(BigInteger assetType) {
        this.assetType = assetType;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public String getAssetManufacturerName() {
        return assetManufacturerName;
    }

    public void setAssetManufacturerName(String assetManufacturerName) {
        this.assetManufacturerName = assetManufacturerName;
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

    public BigInteger getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(BigInteger approvedBy) {
        this.approvedBy = approvedBy;
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

    public BigInteger getHardwareConfiguration() {
        return hardwareConfiguration;
    }

    public void setHardwareConfiguration(BigInteger hardwareConfiguration) {
        this.hardwareConfiguration = hardwareConfiguration;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}

