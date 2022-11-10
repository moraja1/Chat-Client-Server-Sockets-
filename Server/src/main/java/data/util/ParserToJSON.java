package data.util;


import data.model.repository.User;
import data.util.jsonAdapters.UserAdapter;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public class ParserToJSON {
    private static final JsonbConfig config = new JsonbConfig().withAdapters(new UserAdapter());

    public static String UserToJson(User user){
        Jsonb jsonb = JsonbBuilder.create(config);
        String userJSON = jsonb.toJson(user);
        return userJSON;
    }
    public static User JsonToUser(String jsonUser){
        Jsonb jsonb = JsonbBuilder.create(config);
        User user = jsonb.fromJson(jsonUser, User.class);
        return user;
    }
}
