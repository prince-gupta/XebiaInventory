package com.xebia.services;

import com.xebia.entities.AssignAssetMail;
import org.springframework.mail.MailException;

import java.util.List;

/**
 * Created by Pgupta on 29-07-2016.
 */
public interface IMailingService {

    public boolean sendAssetAssignmentMail(AssignAssetMail dto) throws MailException;

    public boolean sendRegisterMail(AssignAssetMail dto) throws MailException;

    public boolean sendAssetExpiryMail(List<AssignAssetMail> dto) throws MailException;
}
