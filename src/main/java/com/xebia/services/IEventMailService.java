package com.xebia.services;

import com.xebia.entities.EventMail;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Pgupta on 12-11-2016.
 */
public interface IEventMailService {

    public void create(EventMail eventMail);

    public EventMail getById(BigInteger id);

    public void update(EventMail eventMail);

    public List<EventMail> fetchMailObjectsNeedToSend();

    public BigInteger getRetires(EventMail eventMail);

    public void registerRetry(EventMail eventMail);

    public boolean isSoftErrorPresent(EventMail eventMail);
}
