package com.xebia.dto;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Pgupta on 06-09-2016.
 */
public class AssetRequestDTO {
    BigInteger assetType;
    String userName;
    Date dateTillValid;
    String remarks;

    public BigInteger getAssetType() {
        return assetType;
    }

    public void setAssetType(BigInteger assetType) {
        this.assetType = assetType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDateTillValid() {
        return dateTillValid;
    }

    public void setDateTillValid(Date dateTillValid) {
        this.dateTillValid = dateTillValid;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
