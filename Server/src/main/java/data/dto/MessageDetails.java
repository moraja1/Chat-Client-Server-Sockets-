package data.dto;

import data.model.repository.Message;
import jakarta.json.bind.annotation.JsonbProperty;

import java.time.LocalDate;

public class MessageDetails {
    private String message;
    private String remitent;
    private String destinatary;
    private LocalDate dateTime;

    public MessageDetails() {
    }

    public MessageDetails(Message message) {
        this.message = message.getMessage();
        this.remitent = message.getRemitent().getUsername();
        this.destinatary = message.getDestinatary().getUsername();
        this.dateTime = message.getDateTime().toLocalDateTime().toLocalDate();
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
    public LocalDate getDateTime() {
        return dateTime;
    }
    @JsonbProperty("dateTime")
    public void setDateTime(LocalDate dateTime) {
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
