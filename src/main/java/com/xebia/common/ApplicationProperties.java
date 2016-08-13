package com.xebia.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Pgupta on 12-08-2016.
 */
@Component
@ConfigurationProperties
public class ApplicationProperties {

    private String tempFilePath;

    @Autowired
    public ApplicationProperties(@Value("${app.tempFilePath}") String tempFilePath){
        this.tempFilePath = tempFilePath;
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }
}
