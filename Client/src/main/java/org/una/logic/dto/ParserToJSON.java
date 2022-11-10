package org.una.logic.dto;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.una.presentation.model.Message;
import org.una.presentation.model.User;

import java.util.ArrayList;
import java.util.List;

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
    public static String MessageToJson(User user){
        Jsonb jsonb = JsonbBuilder.create();
        String userJSON = jsonb.toJson(user);
        return userJSON;
    }
    public static Message JsonToMessage(String messageJson) {
        Jsonb jsonb = JsonbBuilder.create();
        Message message = jsonb.fromJson(messageJson, Message.class);
        return message;
    }

    public static List<Message> JsonToMessageList(String messagesJson) {
        Jsonb jsonb = JsonbBuilder.create();
        List<Message> messageList = new ArrayList<>();
        messageList = jsonb.fromJson(messagesJson, new ArrayList<Message>(){}.getClass().getGenericSuperclass());
        return messageList;
    }
}
