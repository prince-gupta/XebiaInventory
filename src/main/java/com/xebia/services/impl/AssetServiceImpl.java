package com.xebia.services.impl;

import com.xebia.common.Constants;
import com.xebia.dto.*;
import com.xebia.enums.ApprovalStateEnum;
import com.xebia.enums.AssetStatus;
import com.xebia.common.Utility;
import com.xebia.dao.*;
import com.xebia.entities.AssignAssetMail;
import com.xebia.entities.*;
import com.xebia.enums.MailStatus;
import com.xebia.exception.ApplicationException;
import com.xebia.exception.FileException;
import com.xebia.messaging.JMSMailService;
import com.xebia.services.IAssetService;
import com.xebia.services.excel.ExcelService;
import com.xebia.services.print.IPrintService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    @Autowired
    UserDAO userDAO;

    @Autowired
    IPrintService printService;

    @Autowired
    AssignAssetMailDAO assignAssetMailDAO;

    @Autowired
    ExcelService excelService;

    @Autowired
    Environment environment;

    @Autowired
    AssetApprovalDAO assetApprovalDAO;

    @Override
    public String createAssetType(AssetType assetType) {
        assetTypeDAO.create(assetType);
        return "OK";
    }

    @Override
    public Map<String, List<AssetTypeDto>> getAllAssetType() {
        List<AssetTypeDto> resutList = new ArrayList<>();
        List<AssetType> assetTypes = assetTypeDAO.getAll();

        Map<BigInteger, Map<String, Map<String, Integer>>> map = new HashMap<>();
        for (AssetType assetType : assetTypes) {
            List<Asset> assetList = assetDAO.getByTypeId(assetType.getId());
            long availableCount = assetList
                    .stream()
                    .filter(a -> a.getEmployee() == null).count();

            AssetTypeDto assetTypeDto = new AssetTypeDto();
            assetTypeDto.setNumberOfAsset(assetList.size());
            assetTypeDto.setType(assetType.getType());
            assetTypeDto.setId(assetType.getId());
            assetTypeDto.setAvailableAssets((int) availableCount);
            for (Asset asset : assetList) {
                if (map.get(assetType.getId()) == null) {
                    Map<String, Map<String, Integer>> manuMap = new HashMap<>();
                    Map<String, Integer> statsMap = new HashMap<>();
                    statsMap.put("T", 1);
                    if (asset.getEmployee() == null)
                        statsMap.put("A", 1);
                    else
                        statsMap.put("A", 0);
                    manuMap.put(asset.getAssetManufacturer().getName(), statsMap);
                    map.put(assetType.getId(), manuMap);
                } else {
                    Map<String, Map<String, Integer>> manuMap = map.get(assetType.getId());
                    if (manuMap.get(asset.getAssetManufacturer().getName()) == null) {
                        Map<String, Integer> statsMap = new HashMap<>();
                        statsMap.put("T", 1);
                        if (asset.getEmployee() == null)
                            statsMap.put("A", 1);
                        else
                            statsMap.put("A", 0);
                        manuMap.put(asset.getAssetManufacturer().getName(), statsMap);
                        map.put(assetType.getId(), manuMap);
                    } else {
                        Map<String, Integer> statsMap = manuMap.get(asset.getAssetManufacturer().getName());
                        int total = statsMap.get("T") + 1;
                        int available = statsMap.get("A");
                        if (asset.getEmployee() == null) {
                            available = available + 1;
                        }
                        statsMap.put("T", total);
                        statsMap.put("A", available);
                        manuMap.put(asset.getAssetManufacturer().getName(), statsMap);
                        map.put(assetType.getId(), manuMap);
                    }
                }
            }
            resutList.add(assetTypeDto);

        }
        Map result = new HashMap<>();
        result.put("AssetDTOResult", resutList);
        result.put("ManufactureMap", map);
        return result;
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
        if (assetDto.getHardwareConfiguration() != null) {
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
        Employee approver = null;
        if (employee.getApprovalsRequired().equals("Y"))
            approver = employeeDAO.getById(assetDto.getApprovedBy());
        dbAsset.setEmployee(employee);
        assetDAO.update(dbAsset);
        dbAsset = assetDAO.getById(assetDto.getAssetId());
        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setAsset(dbAsset);
        if (approver != null)
            assetHistory.setEmployee1(approver);
        assetHistory.setEmployee2(employee);
        assetHistory.setIssueDate(assetDto.getDateOfIssue());
        assetHistory.setValidTill(assetDto.getDateTillValid());
        assetHistory.setStatus(AssetStatus.ISSUED.getValue());
        User updatedBy = userDAO.getUserByUName(assetDto.getUserName());
        assetHistory.setUpdatedBy(updatedBy);
        assetHistoryDAO.create(assetHistory);
        jmsMailService.registerAssignAssetMail(getAssignAssetMailDto(dbAsset, employee, approver, assetDto, AssetStatus.ISSUED, updatedBy));
        return "OK";
    }

    private AssignAssetMail getAssignAssetMailDto(Asset asset, Employee employee, Employee approver, AssetDto assetDto, AssetStatus status, User updatedBy) {
        AssignAssetMail assignAssetMailDto = new AssignAssetMail();
        assignAssetMailDto.setStatus(MailStatus.NOT_SENT.getValue());
        assignAssetMailDto.setEmployee(employee);
        assignAssetMailDto.setApprover(approver);
        assignAssetMailDto.setAsset(asset);
        assignAssetMailDto.setAssetStatus(status.getValue());
        assignAssetMailDto.setUpdatedDate(new Date());
        assignAssetMailDto.setIssuedBy(updatedBy);
        return assignAssetMailDto;
    }

    public String unAssignAsset(AssetDto assetDto) {
        Asset dbAsset = assetDAO.getById(assetDto.getAssetId());
        Employee employee = employeeDAO.getById(assetDto.getEmployee());
        dbAsset.setEmployee(null);
        User updateBy = userDAO.getUserByUName(assetDto.getUserName());
        List<AssetHistory> dbAssetHistories = assetHistoryDAO.getIssuedNExpiredHistory(assetDto.getEmployee(), assetDto.getAssetId());
        if (dbAssetHistories != null && dbAssetHistories.size() > 0) {
            AssetHistory dbAssetHistory = dbAssetHistories.get(0);
            dbAssetHistory.setStatus(AssetStatus.RETURNED.getValue());
            dbAssetHistory.setReturnedDate(new Date());
            dbAssetHistory.setUpdatedBy(updateBy);
            assetDAO.update(dbAsset);
            assetHistoryDAO.update(dbAssetHistory);
            jmsMailService.registerReturnedAssetMail(getAssignAssetMailDto(dbAsset, employee, null, assetDto, AssetStatus.RETURNED, updateBy));
            return "OK";
        } else {
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
    public void deleteAsset(String id) throws ApplicationException {
        Asset asset = assetDAO.getById(new BigInteger(id));
        if (asset.getEmployee() != null)
            throw new ApplicationException("ASSET_ASSINGMENT_ERROR : " + asset.getEmployee().getFullName());
        List<AssetHistory> assetHistoryList = assetHistoryDAO.getHistoryByAsset(asset);
        for (AssetHistory assetHistory : assetHistoryList) {
            assetHistoryDAO.delete(assetHistory);
        }
        List<AssignAssetMail> assignAssetMails = assignAssetMailDAO.getByAsset(asset);
        for (AssignAssetMail assignAssetMail : assignAssetMails) {
            assignAssetMailDAO.delete(assignAssetMail);
        }
        assetDAO.delete(asset);
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
    public List<Asset> fetchAvailableAssetByTypeAndManu(BigInteger typeId, BigInteger manuId) {

        return assetDAO.getAvailableByTypeId(typeId, manuId);
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
                assetDto.setEmployee(employeeID);
                if (dbAssetHistory.getEmployee2().getApprovalsRequired() == "Y")
                    assetDto.setApprovedBy(dbAssetHistory.getEmployee1().getId());
                if (Utility.isExpired(new Date(dbAssetHistory.getValidTill().getTime())) && !dbAssetHistory.getStatus().equals(AssetStatus.EXPIRED.getValue())) {
                    dbAssetHistory.setStatus(AssetStatus.EXPIRED.getValue());
                    assetDto.setStatus(AssetStatus.EXPIRED.getValue());
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
    public void deleteAssetManufacturer(String id) throws ApplicationException {
        try {
            AssetManufacturer assetManufacturer = manufacturerDAO.getById(new BigInteger(id));
            manufacturerDAO.delete(assetManufacturer);
        } catch (Exception e) {
            throw new ApplicationException("");
        }
    }

    @Override
    public String updateAssetManufacturer(AssetManufacturer assetManufacturer) {
        AssetManufacturer dbAssetType = manufacturerDAO.getById((assetManufacturer.getId()));
        if (StringUtils.isNotBlank(assetManufacturer.getName()))
            dbAssetType.setName(assetManufacturer.getName());
        if (StringUtils.isNotBlank(assetManufacturer.getAddress()))
            dbAssetType.setAddress(assetManufacturer.getAddress());
        if (StringUtils.isNotBlank(assetManufacturer.getEmail()))
            dbAssetType.setEmail(assetManufacturer.getEmail());
        if (StringUtils.isNotBlank(assetManufacturer.getMobile()))
            dbAssetType.setMobile(assetManufacturer.getMobile());
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

    public void deleteHardwareConfig(String id) throws ApplicationException {
        HardwareConfiguration hardwareConfiguration = hardwareConfigurationDAO.getById(new BigInteger(id));
        try {
            hardwareConfigurationDAO.delete(hardwareConfiguration);
        } catch (Exception e) {
            throw new ApplicationException("");
        }
    }

    public List<AssetHistoryDTO> getAssetsHistory(BigInteger employeeId) {
        List<AssetHistory> assetHistoryList = assetHistoryDAO.getHistoryByEmployee(employeeId);
        List<AssetHistoryDTO> assetHistoryDTOs = new ArrayList<>();
        for (AssetHistory assetHistory : assetHistoryList) {
            AssetHistoryDTO assetHistoryDTO = new AssetHistoryDTO();
            assetHistoryDTO.setStatus(assetHistory.getStatus());
            assetHistoryDTO.setAssetName(assetHistory.getAsset().getName());
            assetHistoryDTO.setSerialNumber(assetHistory.getAsset().getSerialNumber());
            if (assetHistory.getEmployee2().getApprovalsRequired() == "Y") {
                assetHistoryDTO.setApproverFirstName(assetHistory.getEmployee1().getFirstName());
                assetHistoryDTO.setApproverLastName(assetHistory.getEmployee1().getLastName());
            }
            assetHistoryDTO.setDateOfIssue(assetHistory.getIssueDate());
            assetHistoryDTO.setDateTillValid(assetHistory.getValidTill());
            assetHistoryDTO.setReturnedDate(assetHistory.getReturnedDate());
            assetHistoryDTO.setUpdaterFirstName(assetHistory.getUpdatedBy().getEmployee().getFirstName());
            assetHistoryDTO.setUpdaterLastName(assetHistory.getUpdatedBy().getEmployee().getLastName());
            assetHistoryDTOs.add(assetHistoryDTO);
        }
        return assetHistoryDTOs;
    }

    public List<AssetDto> getExpirationReportForDashboard() {
        List<AssetDto> assetDtos = new ArrayList<>();
        int dayToExpire = 2;
        Date input = new Date();
        while (dayToExpire >= 0) {
            LocalDate todayDateTemp = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate expiryDateTemp = todayDateTemp.plus(dayToExpire, ChronoUnit.DAYS);
            Date expiryDate = Date.from(expiryDateTemp.atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<AssetHistory> goingToExpireHistory = assetHistoryDAO.getGoingToExpireAssetHistory(expiryDate);
            for (AssetHistory history : goingToExpireHistory) {
                AssetDto assetDto = new AssetDto();
                assetDto.setStatus(AssetStatus.EXPIRING.getValue());
                assetDto.setName(history.getAsset().getName());
                assetDto.setSerialNumber(history.getAsset().getSerialNumber());
                assetDto.setEmployeeName(history.getEmployee2().getFirstName() + " " + history.getEmployee2().getLastName() + "(" + history.getEmployee2().getECode() + ")");
                assetDto.setDateTillValid(history.getValidTill());
                assetDtos.add(assetDto);
            }
            dayToExpire--;
        }

        LocalDate todayDateTemp = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date expiryDate = Date.from(todayDateTemp.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<AssetHistory> expiredHistory = assetHistoryDAO.getExpiredAssetHistory(expiryDate);
        for (AssetHistory history : expiredHistory) {
            AssetDto assetDto = new AssetDto();
            assetDto.setStatus(AssetStatus.EXPIRED.getValue());
            assetDto.setName(history.getAsset().getName());
            assetDto.setSerialNumber(history.getAsset().getSerialNumber());
            assetDto.setEmployeeName(history.getEmployee2().getFirstName() + " " + history.getEmployee2().getLastName() + "(" + history.getEmployee2().getECode() + ")");
            assetDto.setDateTillValid(history.getValidTill());
            assetDtos.add(assetDto);
        }
        return assetDtos;
    }

    public File getEmployeeAssetsFileParam(BigInteger employeeId, String username) {
        List<AssetDto> assetList = getEmployeeAsset(employeeId);
        List<AssetFreeMarkerDto> freeMarkerDtos = new ArrayList<>();
        for (AssetDto assetDto : assetList) {
            AssetFreeMarkerDto assetFreeMarkerDto = new AssetFreeMarkerDto();
            assetFreeMarkerDto.setDateTillValid(assetDto.getDateTillValid());
            assetFreeMarkerDto.setDateOfIssue(assetDto.getDateOfIssue());
            assetFreeMarkerDto.setName(assetDto.getName());
            assetFreeMarkerDto.setSerialNumber(assetDto.getSerialNumber());
            User user = userDAO.getUserByUName(username);
            assetFreeMarkerDto.setIssuedByName(user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName());
            if (assetDto.getApprovedBy() != null) {
                Employee approver = employeeDAO.getById(assetDto.getApprovedBy());
                assetFreeMarkerDto.setApproverName(approver.getFirstName() + " " + approver.getLastName());
            }
            freeMarkerDtos.add(assetFreeMarkerDto);
        }

        File resultFile = null;
        if (assetList.size() > 0) {
            AssetDto assetDto = assetList.get(0);
            Employee employee = employeeDAO.getById(assetDto.getEmployee());
            Map modelData = new HashMap<>();
            modelData.put("fullName", employee.getFirstName() + " " + employee.getLastName());
            modelData.put("eCode", employee.getECode());
            modelData.put("mobile", employee.getMobile());
            modelData.put("email", employee.getEmail());
            modelData.put("assets", freeMarkerDtos);
            resultFile = printService.generatePDF(modelData);
        }
        return resultFile;
    }

    public List<Asset> searchAsset(AssetDto assetDto) {
        AssetManufacturer assetManufacturer = null;
        AssetType assetType = null;
        if (assetDto.getAssetManufacturer() != null)
            assetManufacturer = manufacturerDAO.getById(assetDto.getAssetManufacturer());
        if (assetDto.getAssetType() != null)
            assetType = assetTypeDAO.getById(assetDto.getAssetType());
        Asset asset = new Asset();
        asset.setName(assetDto.getName());
        asset.setSerialNumber(assetDto.getSerialNumber());
        asset.setAssetManufacturer(assetManufacturer);
        asset.setAssetType(assetType);
        List<Asset> assetList = assetDAO.getByAssetObject(asset);
        for (Asset assetTemp : assetList) {
            if (assetTemp.getEmployee() == null) {
                Employee dummyEmployee = new Employee();
                dummyEmployee.setFirstName("Not");
                dummyEmployee.setLastName("Issued");
                assetTemp.setEmployee(dummyEmployee);
            }
        }
        return assetList;
    }

    public void processHistoricalAssets() throws ApplicationException, FileException {
        File historicalAssetDir = new File(environment.getProperty(Constants.HIST_ASSETS_TEMP_PATH));
        File[] files = historicalAssetDir.listFiles();
        if (files == null || files.length == 0) {
            throw new FileException("DIR_EMPTY");
        }

        if (files.length > 1) {
            throw new FileException("MORE_FILES");
        }
        excelService.importToDB(files[0]);
    }

    public void requestAsset(AssetRequestDTO assetRequestDTO) throws ApplicationException {
        AssetApproval assetApproval = new AssetApproval();
        User user = userDAO.getUserByUName(assetRequestDTO.getUserName());
        assetApproval.setEmployee(user.getEmployee());
        assetApproval.setDateTillValid(assetRequestDTO.getDateTillValid());
        assetApproval.setAssetType(assetTypeDAO.getById(assetRequestDTO.getAssetType()));
        assetApproval.setRemarks(assetRequestDTO.getRemarks());
        assetApproval.setStatus(ApprovalStateEnum.SENT.getDbName());
        assetApproval.setSubmittedDate(new Date());
        assetApprovalDAO.create(assetApproval);
    }

    public List<AssetApprovalDTO> fetchAssetApprovals(String userName) throws ApplicationException {
        List<AssetApproval> assetApprovals = null;
        if (userName != null) {
            User user = userDAO.getUserByUName(userName);
            assetApprovals = assetApprovalDAO.getAll(user.getEmployee().getId());
        } else {
            assetApprovals = assetApprovalDAO.getAll();
        }
        return changeToAssetApprovalDTOs(assetApprovals);
    }

    public String getApprovalsBadgeCount() throws ApplicationException {
        List list = assetApprovalDAO.getApprovalsCount(ApprovalStateEnum.SENT.getDbName(), ApprovalStateEnum.PENDING.getDbName());
        if (list != null)
            return list.size() + "";
        return "";
    }

    public List<AssetApprovalDTO> searchApprovals(AssetApprovalDTO searchDto) throws ApplicationException {
        List<AssetApproval> dbAssetApprovals = new ArrayList<>();
        if (!searchDto.isShowApproved() && (searchDto.getIncidentId() != null)) {
            AssetApproval assetApproval = assetApprovalDAO.getById(searchDto.getIncidentId());
            dbAssetApprovals.add(assetApproval);
        } else {
            dbAssetApprovals = assetApprovalDAO.getByStatus(ApprovalStateEnum.APPROVED.getDbName());
        }
        return changeToAssetApprovalDTOs(dbAssetApprovals);
    }

    public void updateAssetApproval(AssetApprovalDTO assetApprovalDTO) throws ApplicationException {
        AssetApproval assetApproval = assetApprovalDAO.getById(assetApprovalDTO.getIncidentId());
        User approvedBy = userDAO.getUserByUName(assetApprovalDTO.getModifiedBy());
        assetApproval.setUser(approvedBy);
        assetApproval.setStatus(assetApprovalDTO.getStatus());
        assetApproval.setRemarks2(assetApprovalDTO.getRemark());
        assetApprovalDAO.update(assetApproval);
    }

    public List<AssetApprovalDTO> fetchPendingApprovals() throws ApplicationException{
        return changeToAssetApprovalDTOs(assetApprovalDAO.getPendingApprovals());
    }

    private List<AssetApprovalDTO> changeToAssetApprovalDTOs(List<AssetApproval> assetApprovals){
        List<AssetApprovalDTO> assetApprovalDTOs = new ArrayList<>();

        for (AssetApproval assetApproval : assetApprovals) {
            AssetApprovalDTO assetApprovalDTO = new AssetApprovalDTO();
            assetApprovalDTO.setIncidentId(assetApproval.getId());
            assetApprovalDTO.setAssetType(assetApproval.getAssetType().getType());
            assetApprovalDTO.setModifiedBy(assetApproval.getUser() != null ? assetApproval.getUser().getEmployee().getFullName() : "");
            assetApprovalDTO.setRaisedBy(assetApproval.getEmployee().getFullName());
            assetApprovalDTO.setEmployeeCode(assetApproval.getEmployee().getECode());
            assetApprovalDTO.setDateTillValid(assetApproval.getDateTillValid());
            assetApprovalDTO.setSubmittedDate(assetApproval.getSubmittedDate());
            assetApprovalDTO.setRemark(assetApproval.getRemarks2());
            assetApprovalDTO.setSpecificRequirement(assetApproval.getRemarks());
            assetApprovalDTO.setStatus(assetApproval.getStatus());
            assetApprovalDTO.setDisplayStatus(ApprovalStateEnum.getByDBName(assetApproval.getStatus()).getDisplayName());
            assetApprovalDTO.setPending(Utility.isExpired(new Date(assetApproval.getSubmittedDate().getTime())) && (ApprovalStateEnum.SENT.getDbName().equals(assetApproval.getStatus())));
            assetApprovalDTOs.add(assetApprovalDTO);
        }
        return assetApprovalDTOs;
    }
}
