package com.xebia.services.excel.impl;

import com.xebia.common.Utility;
import com.xebia.entities.Asset;
import com.xebia.entities.Employee;
import com.xebia.services.IAssetService;
import com.xebia.services.excel.ExcelService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by Pgupta on 11-08-2016.
 */

@Service
public class ExcelUtilityServiceImpl implements ExcelService {

    @Autowired
    IAssetService assetService;

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
}
