package com.xebia.dto;

/**
 * Created by Pgupta on 02-08-2016.
 */
public class AuthenticationResponse {

    public AuthenticationResponse(String userName, String token, String message) {
        this.userName = userName;
        this.token = token;
        this.message = message;
    }

    private String userName;

    private String token;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
