package com.xebia.common;

/**
 * Created by Pgupta on 29-07-2016.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfigUtility {

    @Autowired
    Environment env;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setProtocol(env.getProperty(Constants.MAIL_PROTOCOL));
        javaMailSender.setHost(env.getProperty(Constants.MAIL_SMTP_HOST));
        javaMailSender.setPort(Integer.parseInt(env.getProperty(Constants.MAIL_SMTP_PORT)));
        javaMailSender.setUsername(env.getProperty(Constants.MAIL_SUPPORT_USERNAME));
        javaMailSender.setPassword(env.getProperty(Constants.MAIL_SUPPORT_PASSWORD));

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private static Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "false");
        return properties;
    }

}
