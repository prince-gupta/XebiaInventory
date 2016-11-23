package com.xebia.services;

import com.xebia.dto.*;
import com.xebia.entities.*;
import com.xebia.exception.ApplicationException;
import com.xebia.exception.FileException;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by Pgupta on 24-07-2016.
 */
public interface IAssetService {

    public String createAssetType(AssetType assetType, String userName);

    public Map<String,List<AssetTypeDto>> getAllAssetType();

    public String deleteAssetType(String id, String userName);

    public String updateAssetType(AssetType assetType, String userName);

    public String createAsset(AssetDto assetDto);

    public String assignAsset(AssetDto assetDto);

    public String unAssignAsset(AssetDto assetDto);

    public List<Asset> getAllAsset(int offset, int limit);

    public void deleteAsset(String id) throws ApplicationException;

    public String updateAsset(Asset Asset);

    public List<Asset> getAssetByType(BigInteger typeId);

    public List<Asset> fetchAvailableAssetByTypeAndManu(BigInteger typeId, BigInteger manuId);

    public List<AssetDto> getEmployeeAsset(BigInteger employeeID);

    public String createAssetManufacturer(AssetManufacturer assetManufacturer, String userName);

    public List<AssetManufacturer> getAllAssetManufacturer();

    public void deleteAssetManufacturer(String id, String userName) throws ApplicationException;

    public String updateAssetManufacturer(AssetManufacturer assetManufacturer, String userName);

    public List<HardwareConfiguration> getHardwareConfigurations();

    public String createHardwareConfig(HardwareConfiguration hardwareConfiguration);

    public String updateHardwareConfig(HardwareConfiguration hardwareConfiguration);

    public void deleteHardwareConfig(String id) throws ApplicationException;

    public List<AssetHistoryDTO> getAssetsHistory (BigInteger employeeId);

    public List<AssetDto> getExpirationReportForDashboard();

    public File getEmployeeAssetsFileParam(BigInteger employeeId, String userName);

    public Map searchAsset(AssetDto assetDto);

    public void processHistoricalAssets() throws ApplicationException,FileException;

    public void requestAsset(AssetRequestDTO assetRequestDTO) throws ApplicationException;

    public List<AssetApprovalDTO> fetchAssetApprovals(String userName) throws ApplicationException;

    public String getApprovalsBadgeCount() throws ApplicationException;

    public List<AssetApprovalDTO> searchApprovals(AssetApprovalDTO searchDto) throws ApplicationException;

    public void updateAssetApproval(AssetApprovalDTO assetApprovalDTO) throws ApplicationException;

    public List<AssetApprovalDTO> fetchPendingApprovals() throws ApplicationException;

    public long getAssetsCount();
}
