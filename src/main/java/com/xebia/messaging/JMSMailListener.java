package com.xebia.messaging;

import com.xebia.dao.AssetHistoryDAO;
import com.xebia.dao.AssignAssetMailDAO;
import com.xebia.entities.AssetHistory;
import com.xebia.entities.AssignAssetMail;
import com.xebia.enums.AssetStatus;
import com.xebia.enums.MailStatus;
import com.xebia.exception.MailException;
import com.xebia.services.IMailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Pgupta on 02-08-2016.
 */
@Component
public class JMSMailListener {

    @Autowired
    IMailingService mailingService;

    @Autowired
    AssetHistoryDAO assetHistoryDAO;

    @Autowired
    AssignAssetMailDAO assignAssetMailDAO;

    @JmsListener(destination = "assignAssetMailQueue")
    public void sendAssignAssetMails(List<AssignAssetMail> mailDtos) {
        for (AssignAssetMail assignAssetMail : mailDtos) {
            AssetHistory assetHistory = assetHistoryDAO.getHistory(assignAssetMail.getEmployee().getId(), assignAssetMail.getAsset().getAssetId(), AssetStatus.ISSUED).get(0);
            assignAssetMail.setDateOfIssue(assetHistory.getIssueDate());
            assignAssetMail.setDateTillValid(assetHistory.getValidTill());
            try {
                mailingService.sendAssetAssignmentMail(assignAssetMail);
                assignAssetMail.setStatus(MailStatus.SENT.getValue());
                assignAssetMail.setAssetStatus(AssetStatus.ISSUED.getValue());
            } catch (MailException mailException) {
                assignAssetMail.setStatus(MailStatus.resovleStatus(mailException.getMessage()).getValue());
                assignAssetMail.setAssetStatus(AssetStatus.ISSUED.getValue());
            }

            assignAssetMailDAO.update(assignAssetMail);
        }
    }

    @JmsListener(destination = "expiredAssetMailQueue")
    public void sendExpiredAssetMails(List<AssignAssetMail> mailDtos) {
        for (AssignAssetMail assignAssetMail : mailDtos) {
            AssetHistory assetHistory = assetHistoryDAO.getHistory(assignAssetMail.getEmployee().getId(), assignAssetMail.getAsset().getAssetId(), AssetStatus.ISSUED).get(0);
            assignAssetMail.setDateOfIssue(assetHistory.getIssueDate());
            assignAssetMail.setDateTillValid(assetHistory.getValidTill());
        }

        Map<String, List<AssignAssetMail>> employeeMailMap = mailDtos
                .stream()
                .collect(Collectors.groupingBy(
                        mailDto -> mailDto.getEmployee().getECode()
                ));


        for (String ecode : employeeMailMap.keySet()) {
            List<AssignAssetMail> assignAssetMails = employeeMailMap.get(ecode);
            try {
                mailingService.sendAssetExpiryMail(assignAssetMails);
                for (AssignAssetMail assignAssetMail : assignAssetMails) {
                    assignAssetMail.setStatus(MailStatus.SENT.getValue());
                    assignAssetMail.setAssetStatus(AssetStatus.EXPIRED.getValue());
                    assignAssetMailDAO.update(assignAssetMail);
                }
            }
            catch (MailException m){
                for(AssignAssetMail assignAssetMail : assignAssetMails){
                    assignAssetMail.setStatus(MailStatus.resovleStatus(m.getMessage()).getValue());
                    assignAssetMailDAO.update(assignAssetMail);
                }

            }
        }
    }

    @JmsListener(destination = "registerAssetExpiryMailQueue")
    public void processGettingExpiredAssetMail(List<AssignAssetMail> mails) {
        for (AssignAssetMail mail : mails) {
            assignAssetMailDAO.create(mail);
        }
    }

    @JmsListener(destination = "registerAssignAssetMailQueue")
    public void registerAssignAssetMail(AssignAssetMail assignAssetMail) {
        assignAssetMailDAO.create(assignAssetMail);
    }
}
