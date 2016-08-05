package com.xebia.dto;

import java.math.BigInteger;

/**
 * Created by Pgupta on 27-07-2016.
 */
public class PrintDto {

    private BigInteger employeeId;

    private BigInteger assetId;

    public BigInteger getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(BigInteger employeeId) {
        this.employeeId = employeeId;
    }

    public BigInteger getAssetId() {
        return assetId;
    }

    public void setAssetId(BigInteger assetId) {
        this.assetId = assetId;
    }
}
