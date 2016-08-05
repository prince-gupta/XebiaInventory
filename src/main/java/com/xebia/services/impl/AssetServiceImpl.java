package com.xebia.services.impl;

import com.xebia.enums.AssetStatus;
import com.xebia.common.Utility;
import com.xebia.dao.*;
import com.xebia.dto.AssetDto;
import com.xebia.dto.AssetTypeDto;
import com.xebia.entities.AssignAssetMail;
import com.xebia.entities.*;
import com.xebia.enums.MailStatus;
import com.xebia.messaging.JMSMailService;
import com.xebia.messaging.impl.JMSMailServiceImpl;
import com.xebia.services.IAssetService;
import com.xebia.services.IMailingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Pgupta on 24-07-2016.
 */
@Service
public class AssetServiceImpl implements IAssetService {

    @Autowired
    AssetTypeDAO assetTypeDAO;

    @Autowired
    JMSMailService jmsMailService;

    @Autowired
    AssetDAO assetDAO;

    @Autowired
    AssetHistoryDAO assetHistoryDAO;

    @Autowired
    ManufacturerDAO manufacturerDAO;

    @Autowired
    HardwareConfigurationDAO hardwareConfigurationDAO;

    @Autowired
    EmployeeDAO employeeDAO;

    @Override
    public String createAssetType(AssetType assetType) {
        assetTypeDAO.create(assetType);
        return "OK";
    }

    @Override
    public List<AssetTypeDto> getAllAssetType() {
        return assetTypeDAO.getAll();
    }

    @Override
    public String deleteAssetType(String id) {
        AssetType assetType = assetTypeDAO.getById(new BigInteger(id));
        assetTypeDAO.delete(assetType);
        return "OK";
    }

    @Override
    public String updateAssetType(AssetType assetType) {
        AssetType dbAssetType = assetTypeDAO.getById((assetType.getId()));
        dbAssetType.setType(assetType.getType());
        assetTypeDAO.update(dbAssetType);
        return "OK";
    }

    @Override
    public String createAsset(AssetDto assetDto) {
        AssetManufacturer manufacturer = null;
        AssetType assetType = null;
        HardwareConfiguration hardwareConfiguration = null;
        if (assetDto.getAssetManufacturer() != null) {
            manufacturer = manufacturerDAO.getById(assetDto.getAssetManufacturer());
        }
        if (assetDto.getAssetType() != null)
            assetType = assetTypeDAO.getById(assetDto.getAssetType());
        if(assetDto.getHardwareConfiguration() != null){
            hardwareConfiguration = hardwareConfigurationDAO.getById(assetDto.getHardwareConfiguration());
        }
        Asset asset = new Asset();
        asset.setName(assetDto.getName());
        asset.setAssetManufacturer(manufacturer);
        asset.setAssetType(assetType);
        asset.setSerialNumber(assetDto.getSerialNumber());
        asset.setDateOfPurchase(assetDto.getDateOfPurchase());
        asset.setHardwareConfiguration(hardwareConfiguration);
        assetDAO.create(asset);
        return "OK";
    }

    @Override
    public String assignAsset(AssetDto assetDto) {
        Asset dbAsset = assetDAO.getById(assetDto.getAssetId());
        assetDto.setSerialNumber(dbAsset.getSerialNumber());
        assetDto.setName(dbAsset.getName());
        Employee employee = employeeDAO.getById(assetDto.getEmployee());
        Employee approver = employeeDAO.getById(assetDto.getApprovedBy());
        dbAsset.setEmployee(employee);
        assetDAO.update(dbAsset);
        dbAsset = assetDAO.getById(assetDto.getAssetId());
        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setAsset(dbAsset);
        assetHistory.setEmployee1(approver);
        assetHistory.setEmployee2(employee);
        assetHistory.setIssueDate(assetDto.getDateOfIssue());
        assetHistory.setValidTill(assetDto.getDateTillValid());
        assetHistory.setStatus(AssetStatus.ISSUED.getValue());
        assetHistoryDAO.create(assetHistory);
        jmsMailService.registerMail(getAssignAssetMailDto(dbAsset, employee, approver, assetDto));
        return "OK";
    }

    private AssignAssetMail getAssignAssetMailDto(Asset asset, Employee employee, Employee approver, AssetDto assetDto){
        AssignAssetMail assignAssetMailDto = new AssignAssetMail();
        assignAssetMailDto.setStatus(MailStatus.NOT_SENT.getValue());
        assignAssetMailDto.setEmployee(employee);
        assignAssetMailDto.setApprover(approver);
        assignAssetMailDto.setAsset(asset);
        assignAssetMailDto.setAssetStatus(AssetStatus.ISSUED.getValue());
        return  assignAssetMailDto;
    }

    public String unAssignAsset(AssetDto assetDto) {
        Asset dbAsset = assetDAO.getById(assetDto.getAssetId());
        dbAsset.setEmployee(null);
        List<AssetHistory> dbAssetHistories = assetHistoryDAO.getIssuedNExpiredHistory(assetDto.getEmployee(), assetDto.getAssetId());
        if(dbAssetHistories != null && dbAssetHistories.size() > 0) {
            AssetHistory dbAssetHistory = dbAssetHistories.get(0);
            dbAssetHistory.setStatus(AssetStatus.RETURNED.getValue());
            dbAssetHistory.setReturnedDate(new Date());
            assetDAO.update(dbAsset);
            assetHistoryDAO.update(dbAssetHistory);
            return "OK";
        }
        else{
            return "FAIL";
        }
    }

    @Override
    public List<Asset> getAllAsset() {
        List<Asset> assets = assetDAO.getAll();
        for (Asset asset : assets) {
            if (asset.getEmployee() == null) {
                Employee dummyEmployee = new Employee();
                dummyEmployee.setFirstName("Not");
                dummyEmployee.setLastName("Issued");
                asset.setEmployee(dummyEmployee);
            }
        }
        return assets;
    }

    @Override
    public String deleteAsset(String id) {
        Asset Asset = assetDAO.getById(new BigInteger(id));
        assetDAO.delete(Asset);
        return "OK";
    }

    @Override
    public String updateAsset(Asset Asset) {
        /*Asset dbAsset = AssetDAO.getById((Asset.getId()));
        dbAsset.setType(Asset.getType());
        AssetDAO.update(dbAsset);
        return "OK";*/
        return "OK";
    }

    @Override
    public List<Asset> getAssetByType(BigInteger typeId) {

        return assetDAO.getByTypeId(typeId);
    }

    @Override
    public List<Asset> getAvailableAssetByType(BigInteger typeId) {

        return assetDAO.getAvailableByTypeId(typeId);
    }

    @Override
    public List<AssetDto> getEmployeeAsset(BigInteger employeeID) {
        List<Asset> assetList = assetDAO.getByEmployeeId(employeeID);
        List<AssetDto> assetDtos = new ArrayList<>();
        boolean isDataFetched = false;

        for (Asset asset : assetList) {
            AssetDto assetDto = null;
            List<AssetHistory> history = assetHistoryDAO.getIssuedNExpiredHistory(employeeID, asset.getAssetId());
            if (history.size() > 0) {
                assetDto = getAssetDto(asset);
                AssetHistory dbAssetHistory = history.get(0);
                assetDto.setDateOfIssue(dbAssetHistory.getIssueDate());
                assetDto.setDateTillValid(dbAssetHistory.getValidTill());
                assetDto.setStatus(dbAssetHistory.getStatus());
                if (Utility.isExpired(dbAssetHistory.getValidTill()) && !dbAssetHistory.getStatus().equals(AssetStatus.EXPIRED.getValue())) {
                    dbAssetHistory.setStatus(AssetStatus.EXPIRED.getValue());
                    assetHistoryDAO.update(dbAssetHistory);
                }
                isDataFetched = true;
            }
            if (isDataFetched)
                assetDtos.add(assetDto);
        }
        return assetDtos;
    }


    @Override
    public String createAssetManufacturer(AssetManufacturer assetManufacturer) {
        manufacturerDAO.create(assetManufacturer);
        return "OK";
    }

    @Override
    public List<AssetManufacturer> getAllAssetManufacturer() {
        return manufacturerDAO.getAll();
    }

    @Override
    public String deleteAssetManufacturer(String id) {
        AssetManufacturer assetManufacturer = manufacturerDAO.getById(new BigInteger(id));
        manufacturerDAO.delete(assetManufacturer);
        return "OK";
    }

    @Override
    public String updateAssetManufacturer(AssetManufacturer assetManufacturer) {
        AssetManufacturer dbAssetType = manufacturerDAO.getById((assetManufacturer.getId()));
        if (StringUtils.isNotBlank(assetManufacturer.getName()))
            dbAssetType.setName(assetManufacturer.getName());
        if (StringUtils.isNotBlank(assetManufacturer.getAddress()))
            dbAssetType.setName(assetManufacturer.getAddress());
        if (StringUtils.isNotBlank(assetManufacturer.getEmail()))
            dbAssetType.setName(assetManufacturer.getEmail());
        if (StringUtils.isNotBlank(assetManufacturer.getMobile()))
            dbAssetType.setName(assetManufacturer.getMobile());
        manufacturerDAO.update(dbAssetType);
        return "OK";
    }

    private AssetDto getAssetDto(Asset asset) {
        AssetDto assetDto = new AssetDto();
        assetDto.setAssetId(asset.getAssetId());
        assetDto.setName(asset.getName());
        assetDto.setSerialNumber(asset.getSerialNumber());
        assetDto.setDateOfPurchase(asset.getDateOfPurchase());
        assetDto.setAssetManufacturer(asset.getAssetManufacturer().getId());
        assetDto.setAssetManufacturerName(asset.getAssetManufacturer().getName());
        assetDto.setAssetType(asset.getAssetType().getId());
        assetDto.setAssetTypeName(asset.getAssetType().getType());
        return assetDto;
    }

    public List<HardwareConfiguration> getHardwareConfigurations() {
        return hardwareConfigurationDAO.getAll();
    }

    public String createHardwareConfig(HardwareConfiguration hardwareConfiguration) {
        hardwareConfigurationDAO.create(hardwareConfiguration);
        return "OK";
    }

    public String updateHardwareConfig(HardwareConfiguration hardwareConfiguration) {
        HardwareConfiguration dbHardwareConfiguration = hardwareConfigurationDAO.getById(hardwareConfiguration.getId());
        if (StringUtils.isNotBlank(hardwareConfiguration.getName()))
            dbHardwareConfiguration.setName(hardwareConfiguration.getName());
        if (StringUtils.isNotBlank(hardwareConfiguration.getCpu()))
            dbHardwareConfiguration.setCpu(hardwareConfiguration.getCpu());
        if (StringUtils.isNotBlank(hardwareConfiguration.getHdd()))
            dbHardwareConfiguration.setHdd(hardwareConfiguration.getHdd());
        if (StringUtils.isNotBlank(hardwareConfiguration.getRam()))
            dbHardwareConfiguration.setRam(hardwareConfiguration.getRam());

        hardwareConfigurationDAO.update(dbHardwareConfiguration);
        return "OK";
    }

    public List<AssetHistory> getAssetsHistory(BigInteger employeeId) {
        return assetHistoryDAO.getHistoryByEmployee(employeeId);
    }
}
