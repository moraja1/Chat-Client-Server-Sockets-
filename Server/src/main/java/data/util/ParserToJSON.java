package data.util;

import data.dao.UserDAO;
import data.dto.MessageDetails;
import data.model.repository.Message;
import data.model.repository.User;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ParserToJSON {
    public static String UserToJson(User user){
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.toJson(user);
    }
    public static User JsonToUser(String jsonUser){
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(jsonUser, User.class);
    }
    public static String PendingMessagesToJson(List<MessageDetails> pendingMessages) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(pendingMessages);
    }
    public static MessageDetails JsonToMessage(String messageJson) {
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(messageJson, MessageDetails.class);
    }
    public static String MessageToJson(MessageDetails message) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(message);
    }
    public static List<User> JsonToUsers(String userListJson) {
        Jsonb jsonb = JsonbBuilder.create();
        List<User> contactList = new ArrayList<>();
        contactList = jsonb.fromJson(userListJson, new ArrayList<Message>(){}.getClass().getGenericSuperclass());
        return contactList;
    }
}
