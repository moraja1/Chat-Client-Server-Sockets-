package data.util;

import data.model.repository.User;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class ParserToJSON {
    public static String UserToJson(User user){
        Jsonb jsonb = JsonbBuilder.create();
        String userJSON = jsonb.toJson(user);
        return userJSON;
    }
    public static User JsonToUser(String jsonUser){
        Jsonb jsonb = JsonbBuilder.create();
        User user = jsonb.fromJson(jsonUser, User.class);
        return user;
    }
}
