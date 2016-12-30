package com.xebia.messaging;

import com.xebia.dto.EventMailDTO;
import com.xebia.entities.EventMail;

/**
 * Created by Pgupta on 21-09-2016.
 */
public interface ResolverChain {

    public static final String CHARSET_UTF8 = "UTF-8";

    EventMailDTO resolve(EventMail eventMail);
}
