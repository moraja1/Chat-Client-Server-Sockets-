package data.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "message", schema = "chat", catalog = "")
public class Message {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_message")
    private Long idMessageList;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message that = (Message) o;

        if (idMessageList != null ? !idMessageList.equals(that.idMessageList) : that.idMessageList != null)
            return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (dateTime != null ? !dateTime.equals(that.dateTime) : that.dateTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idMessageList != null ? idMessageList.hashCode() : 0;
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
