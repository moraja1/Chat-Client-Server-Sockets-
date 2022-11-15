package data.dto;

import data.dao.UserDAO;
import data.model.repository.Message;
import data.model.repository.User;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MessageDetails {
    private String message;
    private String remitent;
    private String destinatary;
    private LocalDateTime dateTime;

    public MessageDetails() {
    }

    public MessageDetails(Message message) {
        this.message = message.getMessage();
        this.remitent = message.getRemitent().getUsername();
        this.destinatary = message.getDestinatary().getUsername();
        this.dateTime = message.getDateTime().toLocalDateTime();
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
    @JsonbTransient
    public static Message toEntitie(MessageDetails m){
        Message message = new Message();
        UserDAO userDAO = new UserDAO();
        User remitent = userDAO.getSingleObject(m.getRemitent());
        User destinatary = userDAO.getSingleObject(m.getDestinatary());
        message.setMessage(m.getMessage());
        message.setRemitent(remitent);
        message.setDestinatary(destinatary);
        message.setDateTime(Timestamp.valueOf(m.getDateTime()));
        return message;
    }
}
