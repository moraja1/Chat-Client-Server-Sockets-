package org.una.logic.dto;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.una.presentation.model.User;

public class ParserToJSON {
    public static JsonObject UserToJson(User user){
        JsonObject userJSON = Json.createObjectBuilder().add("username", user.getUsername()).
                add("password", user.getPassword()).build();
        return userJSON;
    }
}
