package com.xebia.services.print;

import com.xebia.exception.ApplicationException;

import java.io.File;
import java.util.Map;

/**
 * Created by Pgupta on 12-08-2016.
 */
public interface IPrintService {

    public void generateHTML(Map<String, String> modelData) throws ApplicationException ;

    public File generatePDF(Map<String, String> modelData) throws ApplicationException;


}
