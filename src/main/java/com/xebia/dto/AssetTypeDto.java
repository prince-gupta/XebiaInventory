package com.xebia.dto;

import java.math.BigInteger;

/**
 * Created by Pgupta on 23-07-2016.
 */
public class AssetTypeDto {

    private String type;
    private int numberOfAsset;
    private BigInteger id ;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumberOfAsset() {
        return numberOfAsset;
    }

    public void setNumberOfAsset(int numberOfAsset) {
        this.numberOfAsset = numberOfAsset;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
