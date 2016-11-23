package com.xebia.messaging.resolvers;

import com.xebia.dao.UserDAO;
import com.xebia.dto.EventMailDTO;
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
public class DefaultResolver implements ResolverChain {

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
        return prepareDto(eventMail);
    }

    private EventMailDTO prepareDto(EventMail eventMail) {
        EventMailDTO eventMailDTO = new EventMailDTO();
        eventMailDTO.setSubject(resolveSubject(eventMail.getEvent(), eventMail.getType()));
        eventMailDTO.setTemplateName("default-events.vm");
        User user = userDAO.getById(eventMail.getUser());
        eventMailDTO.setToMail(user.getEmployee().getEmail());
        eventMailDTO = null;
        eventMailDTO.setMessageBody(prepareMessageBody(eventMail, user));
        return eventMailDTO;
    }

    private String prepareMessageBody(EventMail eventMail, User user) {
        Map model = new HashMap<>();
        model.put("fullName", user.getEmployee().getFullName());
        model.put("userName", user.getUsername());
        model.put("type", eventMail.getType());
        model.put("eventMessage", EventEnum.getEvent(eventMail.getEvent()).getMessage());
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine
                , "default-events.vm", CHARSET_UTF8, model);
    }

    private String resolveSubject(String event, String type) {
        String subject;
        switch (EventEnum.getEvent(event)) {
            case ADD:
                subject = "An " + type + " is registered to System.";
                break;
            case DELETE:
                subject = "An " + type + "  is deleted from System.";
                break;
            case UPDATE:
                subject = "Details for " + type + " is modified.";
                break;
            default:
                subject = "Details for " + type + " is modified.";
                break;
        }
        return subject;
    }
}