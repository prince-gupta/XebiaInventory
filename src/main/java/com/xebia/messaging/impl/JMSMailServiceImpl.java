package com.xebia.messaging.impl;

import com.xebia.dao.AssetHistoryDAO;
import com.xebia.dao.AssignAssetMailDAO;
import com.xebia.entities.AssetHistory;
import com.xebia.entities.AssignAssetMail;
import com.xebia.enums.AssetStatus;
import com.xebia.enums.MailStatus;
import com.xebia.messaging.JMSMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pgupta on 02-08-2016.
 */
@Service
public class JMSMailServiceImpl implements JMSMailService {

    @Autowired
    JmsTemplate template;

    @Autowired
    AssignAssetMailDAO assignAssetMailDAO;

    @Autowired
    AssetHistoryDAO assetHistoryDAO;

    @Scheduled(fixedRate = 20000)
    @Override
    public void processUnsentAssetMails() {
        List<AssignAssetMail> unsentMails = assignAssetMailDAO.getMailObjectsByStatus(MailStatus.NOT_SENT.getValue(), MailStatus.PENDING.getValue());
        List<AssignAssetMail> unsentAssignAssetMails = unsentMails
                .stream()
                .filter(e ->
                        e.getAssetStatus().equals(AssetStatus.ISSUED.getValue()))
                .collect(Collectors.toList());

        List<AssignAssetMail> unsentExpireAssetMails = unsentMails
                .stream()
                .filter(e ->
                        e.getAssetStatus().equals(AssetStatus.EXPIRED.getValue()))
                .collect(Collectors.toList());

        if (unsentAssignAssetMails.size() > 0)
            template.convertAndSend("assignAssetMailQueue", unsentAssignAssetMails);

        if (unsentExpireAssetMails.size() > 0)
            template.convertAndSend("expiredAssetMailQueue", unsentExpireAssetMails);
    }


    @PostConstruct
    @Override
    public void registerGettingExpiredAssetMail() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.afterPropertiesSet();
        scheduler.schedule(() -> {
            Date input = new Date();
            int daysToExpire = 2;
            List<AssignAssetMail> mailDtos = new ArrayList<>();
            int dayToExpire = daysToExpire;
            while (dayToExpire >= 0) {
                LocalDate todayDateTemp = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate expiryDateTemp = todayDateTemp.plus(dayToExpire, ChronoUnit.DAYS);
                Date expiryDate = Date.from(expiryDateTemp.atStartOfDay(ZoneId.systemDefault()).toInstant());

                List<AssetHistory> goingToExpireHistory = assetHistoryDAO.getGoingToExpireAssetHistory(expiryDate);
                for (AssetHistory history : goingToExpireHistory) {
                    AssignAssetMail mailDto = new AssignAssetMail();
                    mailDto.setAssetStatus(AssetStatus.EXPIRED.getValue());
                    mailDto.setAsset(history.getAsset());
                    mailDto.setEmployee(history.getEmployee2());
                    mailDto.setApprover(history.getEmployee1());
                    mailDto.setDateTillValid(history.getValidTill());
                    mailDto.setDateOfIssue(history.getIssueDate());
                    mailDto.setStatus(MailStatus.NOT_SENT.getValue());
                    mailDtos.add(mailDto);
                }
                dayToExpire--;
            }
            template.convertAndSend("registerAssetExpiryMailQueue", mailDtos);


        }, new CronTrigger("20 * * * * *"));

    }

    @Override
    public void registerMail(AssignAssetMail assignAssetMail) {
        template.convertAndSend("registerAssignAssetMailQueue", assignAssetMail);
    }

}
