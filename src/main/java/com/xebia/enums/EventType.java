package com.xebia.enums;

/**
 * Created by Pgupta on 12-09-2016.
 */
public enum EventType {
    EMP,
    ASSET,
    ASSET_TYPE,
    ASSET_MANUFACTURER,
    USER,
    ROLE;

    public static EventType getEventType(String value){
        for(EventType eventType : EventType.values()){
            if(eventType.name().equals(value))
                return eventType;
        }
        return null;
    }
}
