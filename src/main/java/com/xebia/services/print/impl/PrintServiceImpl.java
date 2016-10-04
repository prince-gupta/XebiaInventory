package com.xebia.services.print.impl;

import com.xebia.common.Constants;
import com.xebia.common.Utility;
import com.xebia.exception.ApplicationException;
import com.xebia.services.print.IPrintService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * Created by Pgupta on 12-08-2016.
 */
@Service
public class PrintServiceImpl implements IPrintService {


    public void generateHTML(Map<String, String> modelData) throws ApplicationException {

        Configuration cfg = new Configuration();

        try {

            cfg.setClassForTemplateLoading(this.getClass(), "/templates/freemarker");
        } catch (Exception e) {
            throw new ApplicationException("Unable to load Template Directory.");
        }
        try {
            // Load template from source folder
            Template template = cfg.getTemplate("assignAsset.ftl");

            Utility.copy("images/logo.jpg", Utility.getFullTempFilePath("logo.jpg"));
            Writer file = new FileWriter(new File(Utility.getFullTempFilePath(Constants.HTML)));
            template.process(modelData, file);
            file.flush();
            file.close();
        } catch (IOException e) {
            throw new ApplicationException(
                    "Unable copy either logo or Unable to Open HTML file, Stream. Possible reason : Temp path configured is not accessible.");
        } catch (TemplateException e) {
            e.printStackTrace();
            throw new ApplicationException("Unable load Template File for HTML. Possible reason : Corrupted JAR.");
        }
    }

    public File generatePDF(Map<String, String> modelData) throws ApplicationException {
        generateHTML(modelData);

        String htmlFilePath = null;
        try {
            htmlFilePath = new File(Utility.getFullTempFilePath(Constants.HTML)).toURI().toURL().toString();
        } catch (MalformedURLException e) {
            throw new ApplicationException("Unable to Load Generated HTML File : " + htmlFilePath);
        }
        OutputStream dDPFOutputStream = null;
        try {
            dDPFOutputStream = new FileOutputStream(Utility.getFullTempFilePath(Constants.PDF));
        } catch (FileNotFoundException e) {
            throw new ApplicationException("Unable to Open Output Stream for PDF File.");
        }

       /* ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(htmlFilePath);
        renderer.layout();
        try {
            renderer.createPDF(dDPFOutputStream);
        }
         catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            try {
                dDPFOutputStream.close();
            } catch (IOException e) {
                throw new ApplicationException("Unable to close stream.");
            }
        }*/
        return new File(Utility.getFullTempFilePath(Constants.PDF));
    }
}
