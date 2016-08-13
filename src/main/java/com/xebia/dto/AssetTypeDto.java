package com.xebia.dto;

import com.xebia.entities.AssetManufacturer;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created by Pgupta on 23-07-2016.
 */
public class AssetTypeDto {

    private String type;
    private int numberOfAsset;
    private BigInteger id ;
    private int availableAssets;

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

    public int getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(int availableAssets) {
        this.availableAssets = availableAssets;
    }
}
