package org.una.presentation.model;

import jakarta.json.bind.annotation.JsonbProperty;

public class User {

    private Long idUser;
    private String username;
    private String password;
    private boolean connected;
    public User(){}

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(Long idUser, String username, String password) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
    }
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
    @JsonbProperty("connected")
    public boolean isConected() {
        return connected;
    }
    @JsonbProperty("connected")
    public void setConected(boolean connected) {
        this.connected = connected;
    }
}
