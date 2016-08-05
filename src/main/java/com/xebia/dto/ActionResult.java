package com.xebia.dto;

/**
 * Created by Pgupta on 29-07-2016.
 */
import java.util.HashMap;
import java.util.Map;

public class ActionResult {
    public static enum Status{
        SUCCESS,
        FAILURE
    }

    //Represents status of the request
    private Status status = Status.SUCCESS;
    //Contains errors if any.
    private Map error = new HashMap();


    public Map getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    //Contains data if any
    private Map data = new HashMap();
    private Map additionalInfo=new HashMap();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Map getError() {
        return error;
    }

    public void setError(Map error) {
        this.error = error;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public void addData(Object key, Object value) {
        if(data == null)
            data = new HashMap();
        data.put(key, value);
    }
}
