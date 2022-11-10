package org.una.presentation.model;

import jakarta.json.bind.annotation.JsonbProperty;


public class User {
    @JsonbProperty("username")
    private String username;
    @JsonbProperty("password")
    private String password;
    public User(){}
    public User(String username, String password) {
        this.username = username;
        this.password = password;
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
}
