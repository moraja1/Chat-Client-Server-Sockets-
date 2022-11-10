package org.una.presentation.model;

import java.sql.Timestamp;
public class Message {
    private String message;
    private User remitent;
    private User destinatary;
    private Timestamp dateTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }
    public User getRemitent() {
        return remitent;
    }

    public void setRemitent(User remitent) {
        this.remitent = remitent;
    }

    public User getDestinatary() {
        return destinatary;
    }

    public void setDestinatary(User destinatary) {
        this.destinatary = destinatary;
    }
}
