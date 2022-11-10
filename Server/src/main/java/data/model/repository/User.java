package data.model.repository;

import jakarta.json.bind.annotation.JsonbNumberFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "select u from User u"),
        @NamedQuery(name = "User.findByUsernameAndPassword", query = "select u from User u where u.username = :username and u.password = :password"),
        @NamedQuery(name = "User.existsByUsernameAndPassword", query = "select (count(u) > 0) from User u where u.username = :username and u.password = :password"),
        @NamedQuery(name = "User.findByIdUser", query = "select u from User u where u.idUser = :idUser"),
        @NamedQuery(name = "User.findByUsername", query = "select u from User u where u.username = :username")
})
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_user")
    private Long idUser;
    @Basic
    @Column(name = "username")
    private String username;
    @Basic
    @Column(name = "password")
    private String password;
    @OneToMany(mappedBy = "userByIdUser")
    @JsonbTransient
    private Set<Group> groupsByIdUser;
    @OneToMany(mappedBy = "destinatary")
    @JsonbTransient
    private Set<Message> messages;
    @JsonbProperty("idUser")
    public Long getIdUser() {
        return idUser;
    }
    @JsonbProperty("idUser")
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
    @JsonbProperty("username")
    public String getUsername() {
        return username;
    }
    @JsonbProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }
    @JsonbProperty("password")
    public String getPassword() {
        return password;
    }
    @JsonbProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (idUser != null ? !idUser.equals(user.idUser) : user.idUser != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idUser != null ? idUser.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
    @JsonbTransient
    public Set<Group> getGroupsByIdUser() {
        return groupsByIdUser;
    }
    @JsonbTransient
    public void setGroupsByIdUser(Set<Group> groupsByIdUser) {
        this.groupsByIdUser = groupsByIdUser;
    }
    @JsonbTransient
    public Set<Message> getMessageLists() {
        return messages;
    }
    @JsonbTransient
    public void setMessageLists(Set<Message> messages) {
        this.messages = messages;
    }
}
