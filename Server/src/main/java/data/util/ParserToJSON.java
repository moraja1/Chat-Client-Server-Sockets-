package data.util;

import com.google.protobuf.Message;
import data.dao.UserDAO;
import data.dto.MessageDetails;
import data.dto.UserDetails;
import data.model.repository.User;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.StringReader;
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
        JsonReader jsonReader = Json.createReader(new StringReader(userListJson));
        JsonValue jsonValue = jsonReader.read();
        List<User> contactList = new ArrayList<>();
        for (JsonString js : jsonValue.asJsonArray().getValuesAs(JsonString.class)){
            contactList.add(new User(js.getString()));
        }
        return contactList;
    }
    public static UserDetails JsonToContact(String messageJson) {
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(messageJson, UserDetails.class);
    }
    public static String contactToJson(UserDetails contact) {
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.toJson(contact);
    }
    public static List<MessageDetails> JsonToMessages(String messagesJson) {
        Jsonb jsonb = JsonbBuilder.create();
        List<MessageDetails> messageList;
        messageList = jsonb.fromJson(messagesJson, new ArrayList<MessageDetails>(){}.getClass().getGenericSuperclass());
        if(messageList == null){
            messageList = new ArrayList<>();
        }
        return messageList;
    }
}
