package com.xebia.services.impl;

import com.xebia.common.Constants;
import com.xebia.dto.EventMailDTO;
import com.xebia.entities.AssignAssetMail;
import com.xebia.entities.EventMail;
import com.xebia.messaging.ResolverChain;
import com.xebia.services.IMailingService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pgupta on 29-07-2016.
 */
@Service
public class MailingServiceImpl implements IMailingService {

    @Autowired
    VelocityEngine velocityEngine;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    Environment environment;

    @Autowired
    ResolverChain chain;

    @Autowired
    ResourceLoader resourceLoader;

    private static final String CHARSET_UTF8 = "UTF-8";

    @Override
    public boolean sendAssetAssignmentMail(AssignAssetMail dto) {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(dto.getEmployee().getEmail());
                message = addCCToMessage(message);
                message.setSubject(environment.getProperty(Constants.MAIL_ASSET_ASSIGNMENT_SUBJECT));
                Map model = new HashMap<>();
                model.put("dto", dto);

                message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine
                        , "assignment-confirmation.vm", CHARSET_UTF8, model), true);
            };
            this.javaMailSender.send(preparator);

            return true;
        } catch (MailException e) {
            throw new com.xebia.exception.MailException(e.getMessage());
        }
    }

    @Override
    public boolean sendRegisterMail(AssignAssetMail dto) {
        /*MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(dto.getEmail());
            message.setSubject(environment.getProperty(Constants.MAIL_REGISTRATION_SUBJECT));

            Map model = new HashMap<>();
            model.put("dto", dto);

            message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine
                    , "assignment-confirmation.vm", CHARSET_UTF8, model), true);
        };
        this.javaMailSender.send(preparator);*/
        return true;
    }

    @Override
    public boolean sendAssetExpiryMail(List<AssignAssetMail> dtos) throws MailException {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(dtos.get(0).getEmployee().getEmail());
                message.setSubject(environment.getProperty(Constants.MAIL_ASSET_EXPIRING_SUBJECT));

                Map model = new HashMap<>();
                model.put("dtos", dtos);
                model.put("employee", dtos.get(0).getEmployee());

                message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine
                        , "asset-expiry.vm", CHARSET_UTF8, model), true);
            };
            this.javaMailSender.send(preparator);

            return true;
        } catch (MailException e) {
            throw new com.xebia.exception.MailException(e.getMessage());
        }
    }

    @Override
    public boolean sendAssetExpiredMail(List<AssignAssetMail> dtos) throws MailException {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(dtos.get(0).getEmployee().getEmail());
                message = addCCToMessage(message);
                message.setSubject(environment.getProperty(Constants.MAIL_ASSET_EXPIRED_SUBJECT));

                Map model = new HashMap<>();
                model.put("dtos", dtos);
                model.put("employee", dtos.get(0).getEmployee());

                message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine
                        , "asset-expired.vm", CHARSET_UTF8, model), true);
            };
            this.javaMailSender.send(preparator);

            return true;
        } catch (MailException e) {
            throw new com.xebia.exception.MailException(e.getMessage());
        }
    }

    @Override
    public boolean sendAssetReturnedMail(AssignAssetMail dto) throws MailException {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(dto.getEmployee().getEmail());
                message = addCCToMessage(message);
                message.setSubject(environment.getProperty(Constants.MAIL_ASSET_RETURNED_SUBJECT));

                Map model = new HashMap<>();
                model.put("dto", dto);

                message.setText(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine
                        , "returned-confirmation.vm", CHARSET_UTF8, model), true);
            };
            this.javaMailSender.send(preparator);

            return true;
        } catch (MailException e) {
            throw new com.xebia.exception.MailException(e.getMessage());
        }
    }

    @Override
    public boolean sendEventMails(EventMail eventMail) throws MailException {
        try {
            MimeMessagePreparator preparator = mimeMessage -> {
                String cid = "" + new Date().getTime();
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
                message = addCCToMessage(message);
                EventMailDTO eventMailDTO = chain.resolve(eventMail);
                message.setTo(eventMailDTO.getToMail());
                message.setText(eventMailDTO.getMessageBody(), true);
                message.setSubject(eventMailDTO.getSubject());
                message.addInline("<" + cid + ">", resourceLoader.getResource("classpath:images/logo.jpg"));

            };
            this.javaMailSender.send(preparator);
            return true;
        } catch (MailException e) {
            throw new com.xebia.exception.MailException(e.getMessage());
        }
    }

    private MimeMessageHelper addCCToMessage(MimeMessageHelper messageHelper) throws MessagingException {
        String[] ccArray = environment.getProperty(Constants.IT_MAIL_ADDRESSES).split(",");
        messageHelper.setCc(ccArray);
        return messageHelper;
    }
}
