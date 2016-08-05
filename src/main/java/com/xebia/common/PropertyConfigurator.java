package com.xebia.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by Pgupta on 29-07-2016.
 */
@Configuration
public class PropertyConfigurator {

    @Autowired
    Environment environment;

    static PropertyConfigurator instance = new PropertyConfigurator();

    public static PropertyConfigurator getInstance(){
        return instance;
    }

    public String getRegistrationSubject() {
        return environment.getProperty(Constants.MAIL_REGISTRATION_SUBJECT);
    }
}
