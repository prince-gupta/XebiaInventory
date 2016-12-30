package com.xebia.messaging.resolvers;

import com.xebia.dao.ManufacturerDAO;
import com.xebia.dao.UserDAO;
import com.xebia.dto.EventMailDTO;
import com.xebia.entities.AssetManufacturer;
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
public class AssetManufacturerResolver implements ResolverChain{

    @Autowired
    ManufacturerDAO manufacturerDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    VelocityEngine velocityEngine;

    ResolverChain resolverChain;

    public void setResolverChain(ResolverChain resolverChain) {
        this.resolverChain = resolverChain;
    }

    @Override
    public EventMailDTO resolve(EventMail eventMail) {
        if (eventMail.getType().equals(EventType.ASSET_MANUFACTURER.toString())) {
            return prepareDto(eventMail);
        } else {
            return this.resolverChain.resolve(eventMail);
        }
    }

    private EventMailDTO prepareDto(EventMail eventMail) {
        EventMailDTO eventMailDTO = new EventMailDTO();
        eventMailDTO.setSubject(resolveSubject(eventMail.getEvent()));
        eventMailDTO.setTemplateName("asset-manu-events.vm");
        User user = userDAO.getById(eventMail.getUser());
        eventMailDTO.setToMail(user.getEmployee().getEmail());
        eventMailDTO.setMessageBody(prepareMessageBody(eventMail, user));
        return eventMailDTO;
    }

    private String prepareMessageBody(EventMail eventMail, User user) {
        Map model = new HashMap<>();
        AssetManufacturer assetManufacturer = manufacturerDAO.getById(eventMail.getRefId());
        model.put("fullName", user.getEmployee().getFullName());
        model.put("userName", user.getUsername());
        model.put("assetManu", assetManufacturer);
        model.put("eventMessage", EventEnum.getEvent(eventMail.getEvent()).getMessage());
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine
                , "asset-manu-events.vm", CHARSET_UTF8, model);
    }

    private String resolveSubject(String event) {
        String subject;
        switch (EventEnum.getEvent(event)) {
            case ADD:
                subject = "An Asset Manufacturer is registered to System.";
                break;
            case DELETE:
                subject = "An Asset Manufacturer is deleted from System.";
                break;
            case UPDATE:
                subject = "Details for an Asset Manufacturer is modified.";
                break;
            default:
                subject = "Details for an Asset Manufacturer is modified.";
                break;
        }
        return subject;
    }
}