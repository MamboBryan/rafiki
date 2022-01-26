package com.mambo.rafiki.data.entities;

public class Response {

    private Boolean isSuccessful;
    private String message;

    public Boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(Boolean successful) {
        isSuccessful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
