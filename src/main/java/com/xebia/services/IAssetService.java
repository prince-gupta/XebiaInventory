package com.xebia.services;

import com.xebia.dto.AssetDto;
import com.xebia.dto.AssetHistoryDTO;
import com.xebia.dto.AssetTypeDto;
import com.xebia.entities.*;
import com.xebia.exception.ApplicationException;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by Pgupta on 24-07-2016.
 */
public interface IAssetService {

    public String createAssetType(AssetType assetType);

    public Map<String,List<AssetTypeDto>> getAllAssetType();

    public String deleteAssetType(String id);

    public String updateAssetType(AssetType assetType);

    public String createAsset(AssetDto assetDto);

    public String assignAsset(AssetDto assetDto);

    public String unAssignAsset(AssetDto assetDto);

    public List<Asset> getAllAsset();

    public String deleteAsset(String id);

    public String updateAsset(Asset Asset);

    public List<Asset> getAssetByType(BigInteger typeId);

    public List<Asset> getAvailableAssetByType(BigInteger typeId);

    public List<AssetDto> getEmployeeAsset(BigInteger employeeID);

    public String createAssetManufacturer(AssetManufacturer assetManufacturer);

    public List<AssetManufacturer> getAllAssetManufacturer();

    public void deleteAssetManufacturer(String id) throws ApplicationException;

    public String updateAssetManufacturer(AssetManufacturer assetManufacturer);

    public List<HardwareConfiguration> getHardwareConfigurations();

    public String createHardwareConfig(HardwareConfiguration hardwareConfiguration);

    public String updateHardwareConfig(HardwareConfiguration hardwareConfiguration);

    public void deleteHardwareConfig(String id) throws ApplicationException;

    public List<AssetHistoryDTO> getAssetsHistory (BigInteger employeeId);

    public List<AssetDto> getExpirationReportForDashboard();

    public File getEmployeeAssetsFileParam(BigInteger employeeId, String userName);
}
