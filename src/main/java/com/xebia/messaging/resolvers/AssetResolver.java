package com.xebia.messaging.resolvers;

import com.xebia.dao.AssetDAO;
import com.xebia.dao.EmployeeDAO;
import com.xebia.dao.UserDAO;
import com.xebia.dto.EventMailDTO;
import com.xebia.entities.Asset;
import com.xebia.entities.Employee;
import com.xebia.entities.EventMail;
import com.xebia.entities.User;
import com.xebia.enums.EventEnum;
import com.xebia.enums.EventType;
import com.xebia.messaging.ResolverChain;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

@Component
public class AssetResolver implements ResolverChain {

    @Autowired
    AssetDAO assetDao;

    @Autowired
    UserDAO userDAO;

    @Autowired
    VelocityEngine velocityEngine;

    private ResolverChain resolverChain;

    @Override
    public void setNextResolver(ResolverChain nextResolver) {
        this.resolverChain = nextResolver;
    }

    @Override
    public EventMailDTO resolve(EventMail eventMail) {
        if (eventMail.getType().equals(EventType.ASSET.toString())) {
            return prepareDto(eventMail);
        } else {
            return this.resolverChain.resolve(eventMail);
        }
    }

    private EventMailDTO prepareDto(EventMail eventMail) {
        EventMailDTO eventMailDTO = new EventMailDTO();
        eventMailDTO.setSubject(resolveSubject(eventMail.getEvent()));
        eventMailDTO.setTemplateName("asset-events.vm");
        User user = userDAO.getById(eventMail.getUser());
        eventMailDTO.setToMail(user.getEmployee().getEmail());
        eventMailDTO.setMessageBody(prepareMessageBody(eventMail, user));
        return eventMailDTO;
    }

    private String prepareMessageBody(EventMail eventMail, User user) {
        Map model = new HashMap<>();
        Asset asset = assetDao.getById(eventMail.getRefId());
        model.put("fullName", user.getEmployee().getFullName());
        model.put("userName", user.getUsername());
        model.put("asset", asset);
        model.put("eventMessage", EventEnum.getEvent(eventMail.getEvent()).getMessage());
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine
                , "asset-events.vm", CHARSET_UTF8, model);
    }

    private String resolveSubject(String event) {
        String subject;
        switch (EventEnum.getEvent(event)) {
            case ADD:
                subject = "An Asset is registered to System.";
                break;
            case DELETE:
                subject = "An Asset is deleted from System.";
                break;
            case UPDATE:
                subject = "Details for an Asset is modified.";
                break;
            default:
                subject = "Details for an Asset is modified.";
                break;
        }
        return subject;
    }
}