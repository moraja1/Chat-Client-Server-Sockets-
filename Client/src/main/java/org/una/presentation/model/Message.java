package org.una.presentation.model;

import jakarta.json.bind.annotation.JsonbNumberFormat;
import jakarta.json.bind.annotation.JsonbProperty;

import java.sql.Timestamp;
public class Message {
    private String message;
    private String remitent;
    private String destinatary;
    private Timestamp dateTime;

    public Message() {
    }
    @JsonbProperty("message")
    public String getMessage() {
        return message;
    }
    @JsonbProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }
    @JsonbProperty("dateTime")
    @JsonbNumberFormat("#")
    public Timestamp getDateTime() {
        return dateTime;
    }
    @JsonbProperty("dateTime")
    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }
    @JsonbProperty("remitent")
    public String getRemitent() {
        return remitent;
    }
    @JsonbProperty("remitent")
    public void setRemitent(String remitent) {
        this.remitent = remitent;
    }
    @JsonbProperty("destinatary")
    public String getDestinatary() {
        return destinatary;
    }
    @JsonbProperty("destinatary")
    public void setDestinatary(String destinatary) {
        this.destinatary = destinatary;
    }
}
