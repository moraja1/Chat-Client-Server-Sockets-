package data.util.jsonAdapters;

import data.model.repository.User;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.adapter.JsonbAdapter;

public class UserAdapter implements JsonbAdapter<User, JsonObject> {
    @Override
    public JsonObject adaptToJson(User user) throws Exception {
        return Json.createObjectBuilder()
                .add("username", user.getUsername())
                .add("password", user.getPassword())
                .build();
    }

    @Override
    public User adaptFromJson(JsonObject json) throws Exception {
        User user = new User();
        user.setUsername(json.getString("username"));
        user.setPassword(json.getString("password"));
        return user;
    }
}
