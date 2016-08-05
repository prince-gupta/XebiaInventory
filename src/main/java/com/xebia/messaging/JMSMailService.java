package com.xebia.messaging;

import com.xebia.entities.AssignAssetMail;
import org.springframework.stereotype.Service;

/**
 * Created by Pgupta on 02-08-2016.
 */
public interface JMSMailService {

    public void processUnsentAssetMails();

    public void registerGettingExpiredAssetMail();

    public void registerMail(AssignAssetMail assignAssetMail);
}