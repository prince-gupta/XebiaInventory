package com.xebia.services.excel;

import com.xebia.exception.ApplicationException;

import java.io.File;

/**
 * Created by Pgupta on 11-08-2016.
 */
public interface ExcelService {

    public File exportToExcel();

    public int importToDB(File fileToImport) throws ApplicationException;
}
