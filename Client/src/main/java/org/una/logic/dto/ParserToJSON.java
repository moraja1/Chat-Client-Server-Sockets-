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
        return jsonb.toJson(user);
    }
    public static User JsonToUser(String jsonUser){
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(jsonUser, User.class);
    }
    public static String MessageToJson(Message message){
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.toJson(message);
    }
    public static Message JsonToMessage(String messageJson) {
        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(messageJson, Message.class);
    }

    public static List<Message> JsonToMessageList(String messagesJson) {
        Jsonb jsonb = JsonbBuilder.create();
        List<Message> messageList;
        messageList = jsonb.fromJson(messagesJson, new ArrayList<Message>(){}.getClass().getGenericSuperclass());
        if(messageList == null){
            messageList = new ArrayList<>();
        }
        return messageList;
    }

}
