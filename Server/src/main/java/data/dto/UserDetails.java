package data.dto;

import jakarta.json.bind.annotation.JsonbProperty;

public class UserDetails {
    private String username;
    private Boolean connected;

    public UserDetails() {
    }

    public UserDetails(String username) {
        this.username = username;
    }

    @JsonbProperty("username")
    public String getUsername() {
        return username;
    }
    @JsonbProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }
    @JsonbProperty("connected")
    public Boolean getConnected() {
        return connected;
    }
    @JsonbProperty("connected")
    public void setConnected(Boolean connected) {
        this.connected = connected;
    }
}
