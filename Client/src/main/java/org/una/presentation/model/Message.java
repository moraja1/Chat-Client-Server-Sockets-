package org.una.presentation.model;

import jakarta.json.bind.annotation.JsonbProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Message {
    private String message;
    private String remitent;
    private String destinatary;
    private LocalDateTime dateTime;

    public Message() {
    }

    public Message(String message, String remitent, String destinatary, LocalDateTime dateTime) {
        this.message = message;
        this.remitent = remitent;
        this.destinatary = destinatary;
        this.dateTime = dateTime;
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
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    @JsonbProperty("dateTime")
    public void setDateTime(LocalDateTime dateTime) {
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
