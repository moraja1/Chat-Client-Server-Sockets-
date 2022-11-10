package org.una.logic.dto;


import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.adapter.JsonbAdapter;
import org.una.presentation.model.Message;
import org.una.presentation.model.User;

import java.sql.Timestamp;
import java.time.LocalDate;

public class MessageAdapter implements JsonbAdapter<Message, JsonObject> {
    @Override
    public JsonObject adaptToJson(Message message) throws Exception {
        LocalDate localDate = message.getDateTime().toLocalDateTime().toLocalDate();
        return Json.createObjectBuilder()
                .add("message", message.getMessage())
                .add("dateTime", String.valueOf(localDate))
                .add("remitent", message.getRemitent())
                .add("destinatary", message.getDestinatary())
                .build();
    }

    @Override
    public Message adaptFromJson(JsonObject json) throws Exception {
        Message message = new Message();
        LocalDate localDate = LocalDate.parse(json.getString("dateTime"));
        Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());

        message.setMessage(json.getString("message"));
        message.setDateTime(timestamp);
        message.setRemitent(json.getString("remitent"));
        message.setDestinatary(json.getString("destinatary"));

        return message;
    }
}
