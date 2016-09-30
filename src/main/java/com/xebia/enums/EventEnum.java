package com.xebia.enums;

/**
 * Created by Pgupta on 12-09-2016.
 */
public enum EventEnum {
    DELETE("deleted from"),
    UPDATE("modified in"),
    ADD("registered in");

    private String message;

    EventEnum(String message){
        this.message = message;
    }
    public static EventEnum getEvent(String eventName){
        for(EventEnum eventEnum : EventEnum.values()){
            if(eventEnum.name().equals(eventName))
                return eventEnum;
        }
        return null;
    }

    public String getMessage(){
        return message;
    }
}
