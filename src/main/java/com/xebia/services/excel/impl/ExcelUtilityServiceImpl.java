package com.xebia.services.excel.impl;

import com.xebia.common.Utility;
import com.xebia.dao.*;
import com.xebia.dto.AssetDto;
import com.xebia.dto.ExcelImportDto;
import com.xebia.entities.*;
import com.xebia.enums.ExcelMappingEnum;
import com.xebia.exception.ApplicationException;
import com.xebia.exception.ParsingException;
import com.xebia.services.IAssetService;
import com.xebia.services.excel.ExcelService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static com.xebia.enums.ExcelMappingEnum.getByDisplayName;

/**
 * Created by Pgupta on 11-08-2016.
 */

@Service
public class ExcelUtilityServiceImpl implements ExcelService {

    @Autowired
    IAssetService assetService;

    @Autowired
    ExcelMappingDAO excelMappingDAO;

    @Autowired
    EmployeeDAO employeeDAO;

    @Autowired
    AssetTypeDAO assetTypeDAO;

    @Autowired
    ManufacturerDAO manufacturerDAO;

    @Autowired
    HardwareConfigurationDAO hardwareConfigurationDAO;

    @Autowired
    AssetDAO assetDAO;

    public File exportToExcel() {
        HSSFWorkbook workbook = new HSSFWorkbook();

        //Create a blank sheet
        HSSFSheet sheet = workbook.createSheet("Assets Report");

        List<Asset> assetList = assetService.getAllAsset();
        Map<BigInteger, Object[]> data = new TreeMap<>();

        data.put(new BigInteger("0"), new Object[]{"AssetID", "Type", "Asset Name", "Manufacturer", "Serial Number", "Date Of Purchase", "Issued To"});

        for (Asset asset : assetList) {
            Employee employee = asset.getEmployee();
            String issuedTo = "Not Issued";
            if (employee != null) {
                issuedTo = employee.getFirstName() + " " + employee.getLastName();
            }
            data.put(asset.getAssetId(), new Object[]{
                    asset.getAssetId(),
                    asset.getAssetType().getType(),
                    asset.getName(),
                    asset.getAssetManufacturer().getName(),
                    asset.getSerialNumber(),
                    asset.getDateOfPurchase(),
                    issuedTo
            });
        }

        Set<BigInteger> keyset = data.keySet();
        int rownum = 0;
        for (BigInteger key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof BigInteger)
                    cell.setCellValue(((BigInteger) obj).intValue());
                else if (obj instanceof Date)
                    cell.setCellValue(Utility.parseDateToExcelDate((Date) obj));
            }
        }
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("E:\\AssetsReport.xls"));
            workbook.write(out);
            out.close();
            File toExport = new File("E:\\AssetsReport.xls");
            return toExport;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<ExcelImportDto> parseExcelFile(File fileToImport) {
        List<ExcelImportDto> excelImportDtoList = new LinkedList<>();
        try {
            Workbook workbook = WorkbookFactory.create(fileToImport);

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.rowIterator();
            Map<Integer, String> appMapping = prepareExcelMappingMap(sheet.getRow(0));

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //Skipping Header
                if(row.getRowNum() == 0)
                    continue;
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                int index = 0;
                ExcelImportDto excelImportDto = new ExcelImportDto();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    //Check the cell type and format accordingly
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING :
                        case Cell.CELL_TYPE_BLANK:
                            String cellValue = cell.getStringCellValue();
                            ExcelMappingEnum excelMapping = getByDisplayName(appMapping.get(index));
                            if(excelMapping == null){
                                throw new ApplicationException("No Mapping Found for " + appMapping.get(index) + " in System Enum.");
                            }
                            switch (excelMapping) {
                                case DOI:
                                    excelImportDto.setDateOfIssue(StringUtils.isEmpty(cellValue) ? null : new Date(cellValue));
                                    break;
                                case HN:
                                    excelImportDto.setHostName(cellValue);
                                    break;
                                case ASSET_NAME:
                                    excelImportDto.setAssetName(cellValue);
                                    break;
                                case EMPLOYEE_NAME:
                                    excelImportDto.setEmployeeName(cellValue);
                                    break;
                                case DEPART:
                                    excelImportDto.setDepartment(cellValue);
                                    break;
                                case ASNO:
                                    excelImportDto.setSerialNumber(cellValue);
                                    break;
                                case OS:
                                    excelImportDto.setOperatingSystem(cellValue);
                                    break;
                                case MSOFFICE:
                                    excelImportDto.setMsOffice(cellValue);
                                    break;
                                case HDD:
                                    excelImportDto.setHdd(cellValue);
                                    break;
                                case RAM:
                                    excelImportDto.setRam(cellValue);
                                    break;
                                case PROCESSOR:
                                    excelImportDto.setProcessor(cellValue);
                                    break;
                                case WED:
                                    excelImportDto.setWarrantyEndDate(StringUtils.isEmpty(cellValue) ? null : new Date(cellValue));
                                    break;
                                case LB:
                                    excelImportDto.setLaptopBag(cellValue);
                                    break;
                                case MOUSE:
                                    excelImportDto.setMouse(cellValue);
                                    break;
                                case SPK:
                                    excelImportDto.setSpeaker(cellValue);
                                    break;
                                case ARD:
                                    excelImportDto.setReturnDate( StringUtils.isEmpty(cellValue) ? null : new Date(cellValue));
                                    break;
                                case SOU:
                                    excelImportDto.setStatusOfUser(cellValue);
                                    break;
                                case SNO:
                                    break;
                                case ATO:
                                    break;
                                case HP:
                                    excelImportDto.setHeadPhone(cellValue);
                                    break;
                                case MOBILE:
                                    excelImportDto.setMobile(cellValue);
                                    break;
                                default:
                                    throw new ApplicationException("Mapping Not Found");
                            }
                            break;
                        case Cell.CELL_TYPE_NUMERIC :
                            Double numericCellValue = cell.getNumericCellValue();
                            switch (getByDisplayName(appMapping.get(index))) {
                                case DOI:
                                    excelImportDto.setDateOfIssue(cell.getDateCellValue());
                                    break;
                                case HN:
                                    excelImportDto.setHostName(numericCellValue.toString());
                                    break;
                                case ASSET_NAME:
                                    excelImportDto.setAssetName(numericCellValue.toString());
                                    break;
                                case EMPLOYEE_NAME:
                                    excelImportDto.setEmployeeName(numericCellValue.toString());
                                    break;
                                case DEPART:
                                    excelImportDto.setDepartment(numericCellValue.toString());
                                    break;
                                case ASNO:
                                    excelImportDto.setSerialNumber(numericCellValue.toString());
                                    break;
                                case OS:
                                    excelImportDto.setOperatingSystem(numericCellValue.toString());
                                    break;
                                case MSOFFICE:
                                    excelImportDto.setMsOffice(numericCellValue.toString());
                                    break;
                                case HDD:
                                    excelImportDto.setHdd(numericCellValue.toString());
                                    break;
                                case RAM:
                                    excelImportDto.setRam(numericCellValue.toString());
                                    break;
                                case PROCESSOR:
                                    excelImportDto.setProcessor(numericCellValue.toString());
                                    break;
                                case WED:
                                    excelImportDto.setWarrantyEndDate(cell.getDateCellValue());
                                    break;
                                case LB:
                                    excelImportDto.setLaptopBag(numericCellValue.toString());
                                    break;
                                case MOUSE:
                                    excelImportDto.setMouse(numericCellValue.toString());
                                    break;
                                case SPK:
                                    excelImportDto.setSpeaker(numericCellValue.toString());
                                    break;
                                case ARD:
                                    excelImportDto.setReturnDate(cell.getDateCellValue());
                                    break;
                                case SOU:
                                    excelImportDto.setStatusOfUser(numericCellValue.toString());
                                    break;
                                case SNO:
                                    break;
                                case ATO:
                                    break;
                                case HP:
                                    excelImportDto.setHeadPhone(numericCellValue.toString());
                                    break;
                                case MOBILE:
                                    excelImportDto.setMobile(numericCellValue.toString());
                                    break;
                                default:
                                    throw new ApplicationException("Mapping Not Found");
                            }
                            break;
                        default:
                            throw new ApplicationException("Invalid Cell Type : " + cell.getCellType() + "at Index : " + index);
                    }
                    index++;
                }
                excelImportDtoList.add(excelImportDto);
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return excelImportDtoList;
    }

    public int importToDB(File fileToImport) throws ApplicationException{
        List<ExcelImportDto> excelImportDtoList = parseExcelFile(fileToImport);
        for(ExcelImportDto excelImportDto : excelImportDtoList){

        }
           return 0;
    }

    private void f(ExcelImportDto excelImportDto){
        AssetManufacturer assetManufacturer = checkAndCreateAssetManufacturer(excelImportDto.getAssetName().split(" ")[0]);
        HardwareConfiguration hardwareConfiguration = checkAndCreateHardwareConfiguration(excelImportDto);
        AssetType assetType = checkAndCreateAssetType("Laptop");

        AssetDto assetDto = new AssetDto();
        assetDto.setName(excelImportDto.getAssetName());
        assetDto.setAssetManufacturer(assetManufacturer.getId());
        assetDto.setAssetType(assetType.getId());
        assetDto.setSerialNumber(excelImportDto.getSerialNumber());
        assetDto.setDateOfPurchase(excelImportDto.getDateOfIssue());
        assetDto.setHardwareConfiguration(hardwareConfiguration.getId());
        assetService.createAsset(assetDto);
        Asset dbAsset = assetDAO.getByAssetDtoObject(assetDto).get(0);
        AssetDto tempAssetDto = new AssetDto();
        tempAssetDto.setAssetId(tempAssetDto.getAssetId());
        assetService.assignAsset(assetDto);
    }

    private AssetType checkAndCreateAssetType(String assetType){
        List<AssetType> assetTypeList = assetTypeDAO.getByType(assetType);
        if(assetTypeList == null || assetTypeList.size() == 0){
            AssetType assetType1 = new AssetType();
            assetType1.setType(assetType);
            assetTypeDAO.create(assetType1);
            return assetTypeDAO.getByType(assetType).get(0);
        }
        return assetTypeList.get(0);
    }

    private AssetManufacturer checkAndCreateAssetManufacturer(String assetManu){
        List<AssetManufacturer> assetManufacturers= manufacturerDAO.getByName(assetManu);
        if(assetManufacturers == null){
            AssetManufacturer assetManufacturer = new AssetManufacturer();
            assetManufacturer.setName(assetManu);
            manufacturerDAO.create(assetManufacturer);
            return manufacturerDAO.getByName(assetManu).get(0);
        }
        return assetManufacturers.get(0);
    }

    private HardwareConfiguration checkAndCreateHardwareConfiguration(ExcelImportDto excelImportDto){
        HardwareConfiguration hardwareConfiguration = new HardwareConfiguration();
        hardwareConfiguration.setCpu(excelImportDto.getProcessor());
        hardwareConfiguration.setHdd(excelImportDto.getHdd());
        hardwareConfiguration.setRam(excelImportDto.getRam());
        List<HardwareConfiguration> hardwareConfigurations = hardwareConfigurationDAO.getByObject(hardwareConfiguration);
        if(hardwareConfigurations == null || hardwareConfigurations.size() == 0){
            hardwareConfigurationDAO.create(hardwareConfiguration);
            return hardwareConfigurationDAO.getByObject(hardwareConfiguration).get(0);
        }
        return hardwareConfigurations.get(0);
    }

    private Map prepareExcelMappingMap(Row row) throws ParsingException {
        Map<Integer, String> mapping = new HashMap<>();
        Map<String, String> dbMappings = prepareExcelDBMap();
        Iterator<Cell> cellIterator = row.cellIterator();
        int index = 0;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getCellType() != Cell.CELL_TYPE_STRING)
                throw new ParsingException("Invalid Data Type of a Cell in Header ");
            String appColumnName = dbMappings.get(cell.getStringCellValue());
            if (appColumnName == null)
                throw new ParsingException("Undefined Column Name : " + cell.getStringCellValue() + " .Please cross-check you excel mappings.");
            mapping.put(index, appColumnName);
            index++;
        }
        return mapping;
    }

    private Map prepareExcelDBMap() {
        Map<String, String> mappings = new HashMap<>();
        List<ExcelMapping> dbMappings = excelMappingDAO.getAll();
        for (ExcelMapping excelMapping : dbMappings) {
            mappings.put(excelMapping.getExcelColumnName(), excelMapping.getAppColumnName());
        }
        return mappings;
    }
}
