package org.una.presentation.model;

import java.sql.Timestamp;
public class Message {

    private Long idMessageList;
    private String message;
    private Timestamp dateTime;
    private User remitent;
    private User destinatary;

    public Long getIdMessageList() {
        return idMessageList;
    }

    public void setIdMessageList(Long idMessageList) {
        this.idMessageList = idMessageList;
    }

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
