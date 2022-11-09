package data.model.repository;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "select u from User u"),
        @NamedQuery(name = "User.findByUsernameAndPassword", query = "select u from User u where u.username = :username and u.password = :password"),
        @NamedQuery(name = "User.existsByUsernameAndPassword", query = "select (count(u) > 0) from User u where u.username = :username and u.password = :password")
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
    private Set<Group> groupsByIdUser;
    @OneToMany(mappedBy = "destinatary")
    private Set<Message> messages;

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Set<Group> getGroupsByIdUser() {
        return groupsByIdUser;
    }

    public void setGroupsByIdUser(Set<Group> groupsByIdUser) {
        this.groupsByIdUser = groupsByIdUser;
    }

    public Set<Message> getMessageLists() {
        return messages;
    }

    public void setMessageLists(Set<Message> messages) {
        this.messages = messages;
    }
}
