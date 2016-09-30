package com.xebia.messaging;

/**
 * Created by Pgupta on 21-09-2016.
 */
public interface EventTypeResolverChain {

    void setNextResolver(EventTypeResolverChain nextResolver);

    String passControl(String type);
}
