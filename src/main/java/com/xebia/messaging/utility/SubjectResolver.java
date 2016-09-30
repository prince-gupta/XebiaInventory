package com.xebia.messaging.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by Pgupta on 21-09-2016.
 */
@Component
public class SubjectResolver {

    @Autowired
    static Environment environment;

    public static String getSubject(String propertyName){
        return environment.getProperty(propertyName);
    }
}
