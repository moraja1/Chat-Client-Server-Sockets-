package org.una.presentation.model;

import java.io.Serializable;

public class Message implements Serializable{
    User sender;
    String message;

    public Message() {
    }

    public Message(User sedner, String message) {
        this.sender = sedner;
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
