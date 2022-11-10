package data.model.repository;

import jakarta.json.bind.annotation.JsonbNumberFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "message", schema = "chat")
@NamedQueries({
        @NamedQuery(name = "Message.findByDestinatary", query = "select m from Message m where m.destinatary = :destinatary")
})
public class Message {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_message")
    @JsonbTransient
    private Long idMessage;
    @Basic
    @Column(name = "message")
    private String message;
    @Basic
    @Column(name = "date_time")
    private Timestamp dateTime;
    @ManyToOne
    @JoinColumn(name = "remitent", referencedColumnName = "id_user", nullable = false)
    private User remitent;
    @ManyToOne
    @JoinColumn(name = "destinatary", referencedColumnName = "id_user", nullable = false)
    private User destinatary;
    @Column(name = "delivered")
    private Boolean delivered;
    public Boolean isDelivered() {
        return delivered;
    }
    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }
    @JsonbProperty("")
    public Long getIdMessage() {
        return idMessage;
    }
    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message that = (Message) o;

        if (idMessage != null ? !idMessage.equals(that.idMessage) : that.idMessage != null)
            return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (dateTime != null ? !dateTime.equals(that.dateTime) : that.dateTime != null) return false;

        return true;
    }
    @Override
    public int hashCode() {
        int result = idMessage != null ? idMessage.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (dateTime != null ? dateTime.hashCode() : 0);
        return result;
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