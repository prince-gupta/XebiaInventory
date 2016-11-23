package com.xebia.messaging;

import com.xebia.dao.AssetHistoryDAO;
import com.xebia.dao.AssignAssetMailDAO;
import com.xebia.dao.EventMailDAO;
import com.xebia.entities.AssetHistory;
import com.xebia.entities.AssignAssetMail;
import com.xebia.entities.EventMail;
import com.xebia.enums.AssetStatus;
import com.xebia.enums.EventType;
import com.xebia.enums.MailStatus;
import com.xebia.exception.MailException;
import com.xebia.services.IEventMailService;
import com.xebia.services.IMailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
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

    @Autowired
    IEventMailService eventMailService;

    @JmsListener(destination = "assignAssetMailQueue")
    public void sendAssignAssetMails(List<AssignAssetMail> mailDtos) {
        for (AssignAssetMail assignAssetMail : mailDtos) {
            List<AssetHistory> assetHistoryList = assetHistoryDAO.getHistory(assignAssetMail.getEmployee().getId(), assignAssetMail.getAsset().getAssetId(), AssetStatus.ISSUED);
            if (assetHistoryList.size() > 0) {
                AssetHistory assetHistory = assetHistoryList.get(0);
                assignAssetMail.setDateOfIssue(assetHistory.getIssueDate());
                assignAssetMail.setDateTillValid(assetHistory.getValidTill());
                assignAssetMail.setIssuedBy(assetHistory.getUpdatedBy());
                try {
                    mailingService.sendAssetAssignmentMail(assignAssetMail);
                    assignAssetMail.setStatus(MailStatus.SENT.getValue());
                    assignAssetMail.setAssetStatus(AssetStatus.ISSUED.getValue());
                } catch (MailException mailException) {
                    assignAssetMail.setStatus(MailStatus.resovleStatus(mailException.getMessage()).getValue());
                    assignAssetMail.setAssetStatus(AssetStatus.ISSUED.getValue());
                }
                assignAssetMail.setUpdatedDate(new Date());
                assignAssetMailDAO.update(assignAssetMail);
            }
        }
    }

    @JmsListener(destination = "returnedAssetMailQueue")
    public void sendReturnedAssetMails(List<AssignAssetMail> mailDtos) {
        for (AssignAssetMail assignAssetMail : mailDtos) {
            List<AssetHistory> assetHistoryList = assetHistoryDAO.getHistory(assignAssetMail.getEmployee().getId(), assignAssetMail.getAsset().getAssetId(), AssetStatus.RETURNED);
            if (assetHistoryList.size() > 0) {
                AssetHistory assetHistory = assetHistoryList.get(0);
                assignAssetMail.setDateOfIssue(assetHistory.getIssueDate());
                assignAssetMail.setDateofReturned(assetHistory.getReturnedDate());
                assignAssetMail.setIssuedBy(assetHistory.getUpdatedBy());
                try {
                    mailingService.sendAssetReturnedMail(assignAssetMail);
                    assignAssetMail.setStatus(MailStatus.SENT.getValue());
                    assignAssetMail.setAssetStatus(AssetStatus.RETURNED.getValue());
                    assignAssetMail.setUpdatedDate(new Date());
                } catch (MailException mailException) {
                    assignAssetMail.setStatus(MailStatus.resovleStatus(mailException.getMessage()).getValue());
                    assignAssetMail.setAssetStatus(AssetStatus.RETURNED.getValue());
                    assignAssetMail.setUpdatedDate(new Date());
                }

                assignAssetMailDAO.update(assignAssetMail);
            }
        }
    }

    @JmsListener(destination = "expiringAssetMailQueue")
    public void sendExpiringAssetMails(List<AssignAssetMail> mailDtos) {
        for (AssignAssetMail assignAssetMail : mailDtos) {
            AssetHistory assetHistory = assetHistoryDAO.getHistory(assignAssetMail.getEmployee().getId(), assignAssetMail.getAsset().getAssetId(), AssetStatus.ISSUED).get(0);
            assignAssetMail.setDateOfIssue(assetHistory.getIssueDate());
            assignAssetMail.setDateTillValid(assetHistory.getValidTill());
            assignAssetMail.setUpdatedDate(new Date());
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
                    assignAssetMail.setAssetStatus(AssetStatus.EXPIRING.getValue());
                    assignAssetMail.setUpdatedDate(new Date());
                    assignAssetMailDAO.update(assignAssetMail);
                }
            } catch (MailException m) {
                for (AssignAssetMail assignAssetMail : assignAssetMails) {
                    assignAssetMail.setStatus(MailStatus.resovleStatus(m.getMessage()).getValue());
                    assignAssetMail.setUpdatedDate(new Date());
                    assignAssetMailDAO.update(assignAssetMail);
                }

            }
        }
    }

    @JmsListener(destination = "expiredAssetMailQueue")
    public void sendExpiredAssetMails(List<AssignAssetMail> mailDtos) {
        for (AssignAssetMail assignAssetMail : mailDtos) {
            List<AssetHistory> expiredHistoryList = assetHistoryDAO.getHistory(assignAssetMail.getEmployee().getId(), assignAssetMail.getAsset().getAssetId(), AssetStatus.EXPIRED);
            if (expiredHistoryList.size() > 0) {
                AssetHistory assetHistory = expiredHistoryList.get(0);
                assignAssetMail.setDateOfIssue(assetHistory.getIssueDate());
                assignAssetMail.setDateTillValid(assetHistory.getValidTill());
                assignAssetMail.setUpdatedDate(new Date());
            }
        }

        Map<String, List<AssignAssetMail>> employeeMailMap = mailDtos
                .stream()
                .collect(Collectors.groupingBy(
                        mailDto -> mailDto.getEmployee().getECode()
                ));


        for (String ecode : employeeMailMap.keySet()) {
            List<AssignAssetMail> assignAssetMails = employeeMailMap.get(ecode);
            try {
                mailingService.sendAssetExpiredMail(assignAssetMails);
                for (AssignAssetMail assignAssetMail : assignAssetMails) {
                    assignAssetMail.setStatus(MailStatus.SENT.getValue());
                    assignAssetMail.setAssetStatus(AssetStatus.EXPIRED.getValue());
                    assignAssetMail.setUpdatedDate(new Date());
                    assignAssetMailDAO.update(assignAssetMail);
                }
            } catch (MailException m) {
                for (AssignAssetMail assignAssetMail : assignAssetMails) {
                    assignAssetMail.setStatus(MailStatus.resovleStatus(m.getMessage()).getValue());
                    assignAssetMail.setUpdatedDate(new Date());
                    assignAssetMailDAO.update(assignAssetMail);
                }

            }
        }
    }

    @JmsListener(destination = "registerAssetExpiryMailQueue")
    public void processGettingExpiredAssetMail(List<AssignAssetMail> mails) {
        for (AssignAssetMail mail : mails) {
            mail.setUpdatedDate(new Date());
            assignAssetMailDAO.create(mail);
        }
    }

    @JmsListener(destination = "registerAssetExpiredMailQueue")
    public void processExpiredAssetMail(List<AssignAssetMail> mails) {
        for (AssignAssetMail mail : mails) {
            mail.setUpdatedDate(new Date());
            assignAssetMailDAO.create(mail);
        }
    }

    @JmsListener(destination = "registerAssignAssetMailQueue")
    public void registerAssignAssetMail(AssignAssetMail assignAssetMail) {
        assignAssetMailDAO.create(assignAssetMail);
    }

    @JmsListener(destination = "registerReturnedAssetMailQueue")
    public void registerReturnedAssetMail(AssignAssetMail assignAssetMail) {
        assignAssetMailDAO.create(assignAssetMail);
    }

    @JmsListener(destination = "mailQueue")
    public void registerMail(EventMail eventMail) {
        eventMail.setStatus(MailStatus.NOT_SENT.getValue());
        eventMailService.create(eventMail);
    }

    @JmsListener(destination = "unsentEventMailQueue")
    public void sendEventMail(List<EventMail> eventMails) {
        for (EventMail eventMail : eventMails) {
            BigInteger retries = eventMailService.getRetires(eventMail);
            if (eventMailService.isSoftErrorPresent(eventMail)) {
                try {
                    mailingService.sendEventMails(eventMail);
                    eventMail.setStatus(MailStatus.SENT.getValue());
                    eventMailService.update(eventMail);
                } catch (org.springframework.mail.MailException e) {
                    if (((e instanceof MailSendException) || (e instanceof MailAuthenticationException))) {
                        if (retries.intValue() >= 3) {
                            eventMail.setErrorMessage("Unable to send mail . Reason : " + e.getMessage());
                            eventMail.setStatus(MailStatus.RETRYING_FAILED.getValue());
                            eventMail.setIsSoftError("Y");
                        } else {
                            eventMailService.registerRetry(eventMail);
                            eventMail.setStatus(MailStatus.PENDING.getValue());
                        }
                    } else {
                        eventMail.setErrorMessage("Unable to send mail . Reason : " + e.getMessage());
                        eventMail.setStatus(MailStatus.FAILED.getValue());
                        eventMail.setIsSoftError("N");
                    }
                    eventMailService.update(eventMail);
                }
            }
        }
    }
}
