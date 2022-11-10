package data.util.jsonAdapters;

import data.dao.UserDAO;
import data.model.repository.Message;
import data.model.repository.User;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.adapter.JsonbAdapter;

import java.sql.Timestamp;
import java.time.LocalDate;

public class MessageAdapter implements JsonbAdapter<Message, JsonObject> {
    @Override
    public JsonObject adaptToJson(Message message) throws Exception {
        LocalDate localDate = message.getDateTime().toLocalDateTime().toLocalDate();
        return Json.createObjectBuilder()
                .add("message", message.getMessage())
                .add("dateTime", String.valueOf(localDate))
                .add("remitent", message.getRemitent().getUsername())
                .add("destinatary", message.getDestinatary().getUsername())
                .build();
    }

    @Override
    public Message adaptFromJson(JsonObject json) throws Exception {
        Message message = new Message();
        LocalDate localDate = LocalDate.parse(json.getString("dateTime"));
        Timestamp timestamp = Timestamp.valueOf(localDate.atStartOfDay());
        UserDAO dao = new UserDAO();
        User remitent = dao.getSingleObject(json.getString("remitent"));
        User destinatary = dao.getSingleObject(json.getString("destinatary"));

        message.setMessage(json.getString("message"));
        message.setDateTime(timestamp);
        message.setRemitent(remitent);
        message.setDestinatary(destinatary);
        message.setDelivered(false);

        return message;
    }
}
