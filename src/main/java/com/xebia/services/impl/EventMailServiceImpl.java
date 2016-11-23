package com.xebia.services.impl;

import com.xebia.dao.EventMailDAO;
import com.xebia.entities.EventMail;
import com.xebia.enums.MailStatus;
import com.xebia.services.IEventMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 12-11-2016.
 */
@Service
public class EventMailServiceImpl implements IEventMailService {

    @Autowired
    EventMailDAO eventMailDAO;

    public void create(EventMail eventMail){
        eventMail.setRetries(new BigInteger("0"));
        eventMail.setIsSoftError("Y");
        eventMailDAO.create(eventMail);
    }

    public List<EventMail> fetchMailObjectsNeedToSend() {
        return eventMailDAO.getMailObjectsByStatus(MailStatus.NOT_SENT.getValue(), MailStatus.PENDING.getValue(), MailStatus.RETRYING_FAILED.getValue());
    }

    public BigInteger getRetires(EventMail eventMail){
        return eventMailDAO.getRetries(eventMail);
    }

    public void registerRetry(EventMail eventMail){
        BigInteger retries = getRetires(eventMail);
        eventMail.setRetries(retries.add(new BigInteger("1")));
        eventMailDAO.update(eventMail);
    }

    private void updateToDB(EventMail eventMail){
        eventMailDAO.update(eventMail);
    }

    public EventMail getById(BigInteger id){
        return eventMailDAO.getById(id);
    }

    public void update(EventMail eventMail){
        EventMail dbEventMail = getById(eventMail.getId());
        dbEventMail.setStatus(eventMail.getStatus());
        dbEventMail.setErrorMessage(eventMail.getErrorMessage());
        updateToDB(dbEventMail);
    }

    public boolean isSoftErrorPresent(EventMail eventMail){
        return eventMailDAO.isSoftError(eventMail).equals("Y");
    }
}
