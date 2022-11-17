package org.una.logic;

import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;
import org.una.presentation.model.Message;
import org.una.presentation.model.Model;
import org.una.presentation.model.User;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

public class jsonFileAdmin {
    private static final String PATH = "src/main/resources/";
    private static JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    private static File jsonFile;
    private static Map<String, Object> properties = new HashMap<>(1);

    private static void loadFile(String username) {
        String fullPath = new StringBuilder().append(PATH).append(username).append(".json").toString();
        jsonFile = new File(fullPath);
        if(!jsonFile.exists()){
            try {
                jsonFile.createNewFile();
                jsonBuilder.add("contacts", Json.createArrayBuilder().build()).add("conversations", Json.createObjectBuilder().build());
                JsonObject json = jsonBuilder.build();
                OutputStream outStream = Files.newOutputStream(jsonFile.toPath());
                JsonWriter jsonWriter = Json.createWriter(outStream);
                jsonWriter.writeObject(json);
                jsonWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static List<String> contactListFromJason(String username) {
        JsonObject json = getJsonObject(username);
        JsonArray contacts = json.getJsonArray("contacts");
        List<String> contactList = new ArrayList<>();
        for(JsonString v : contacts.getValuesAs(JsonString.class)){
            contactList.add(v.getString());
        }
        return contactList;
    }

    public static List<String> getConversationWith(String username, String contactUsername) {
        JsonObject json = getJsonObject(username);
        JsonArray messagesJson = getJsonConversationsWith(json, contactUsername);
        List<String> messages = new ArrayList<>();
        if(messagesJson == null){
            createMessagesSpace(json, username, contactUsername);
        }else{
            for(JsonObject jv : messagesJson.getValuesAs(JsonObject.class)){
                messages.add(jv.toString());
            }
        }
        return messages;
    }

    private static JsonObject getJsonObject(String username){
        loadFile(username);
        String fileAsJson;
        try {
            fileAsJson = new String(Files.readAllBytes(jsonFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonReader jsonReader = Json.createReader(new StringReader(fileAsJson));
        return jsonReader.readObject();
    }

    private static void saveOnFile(String username, JsonValue result) {
        loadFile(username);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        try{
            OutputStream outStream = Files.newOutputStream(jsonFile.toPath());
            JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(properties);
            JsonWriter jsonWriter = jsonWriterFactory.createWriter(outStream);
            jsonWriter.writeObject(result.asJsonObject());
            jsonWriter.close();
        }catch (Exception e){}
    }
    public static void addNewMessage(String username, String contactUsername, Message message) {
        JsonObject json = getJsonObject(username);

        //-------------------Obtaining Json Messages-----------------------
        JsonArray messages = getJsonConversationsWith(json, contactUsername);
        if(messages == null){
            createMessagesSpace(json, username, contactUsername);
            json = getJsonObject(username);
            messages = getJsonConversationsWith(json, contactUsername);
        }
        //-------------------Create Json to be added----------------------------
        JsonValue patch = Json.createArrayBuilder(messages).add(Json.createObjectBuilder()
                        .add("message", message.getMessage())
                        .add("remitent", message.getRemitent())
                        .add("destinatary", message.getDestinatary())
                        .add("dateTime", message.getDateTime().toString()).build()).build();

        //---------------------Merge it on Messages Json-----------------------
        JsonMergePatch jsonMergePatch = Json.createMergePatch(patch);
        JsonValue result = jsonMergePatch.apply(messages);

        //---------------------Merge it on Conversation Json------------------
        JsonObject conversations = json.getJsonObject("conversations");
        patch = Json.createObjectBuilder().add(contactUsername, result).build();
        jsonMergePatch = Json.createMergePatch(patch);
        result = jsonMergePatch.apply(conversations);

        //----------------------Merge it on File Json-----------------------
        patch = Json.createObjectBuilder().add("conversations", result).build();
        jsonMergePatch = Json.createMergePatch(patch);
        result = jsonMergePatch.apply(json);

        //-------------------------------Save-----------------------------------
        saveOnFile(username, result);
    }
    private static JsonArray getJsonConversationsWith(JsonObject json, String contactUsername){
        JsonObject conversations = json.getJsonObject("conversations");
        return conversations.getJsonArray(contactUsername);
    }
    private static void createMessagesSpace(JsonObject json, String username, String contactUsername) {
        //Creates a space for contact messages
        JsonValue patch = Json.createObjectBuilder().add("conversations", Json.createObjectBuilder().add(contactUsername, Json.createArrayBuilder().build())).build();
        JsonMergePatch jsonMergePatch = Json.createMergePatch(patch);
        JsonValue result = jsonMergePatch.apply(json);
        //Save it on memory
        saveOnFile(username, result);
    }
    public static void addNewContact(String username, String remitent) {
        JsonObject json = getJsonObject(username);
        JsonArray contacts = json.getJsonArray("contacts");
        //-------------------Create Json to be added----------------------------
        JsonValue patch = Json.createObjectBuilder().add("contacts", Json.createArrayBuilder(contacts).add(remitent).build()).build();
        JsonMergePatch jsonMergePatch = Json.createMergePatch(patch);
        JsonValue result = jsonMergePatch.apply(json);
        saveOnFile(username, result);
    }
    public static List<Message> getAllConversations(String username) {
        JsonObject json = getJsonObject(username);
        JsonObject conversations = json.getJsonObject("conversations");
        List<Message> messages = new ArrayList<>();

        if(conversations != null){
            if(!conversations.isEmpty()){
                for(JsonValue jo : conversations.values()){
                    JsonArray v = jo.asJsonArray();
                    for(JsonObject value : v.getValuesAs(JsonObject.class)){
                        messages.add(new Message(value.getString("message"),
                                value.getString("remitent"),
                                value.getString("destinatary"),
                                LocalDateTime.parse(value.getString("dateTime"))));
                    }
                }
            }
        }
        return messages;
    }
    public static String getLogoutContactList(String username) {
        JsonObject json = getJsonObject(username);
        JsonArray contacts = json.getJsonArray("contacts");
        String contactList = contacts.toString();
        return contactList;
    }
    public static void saveCurrentStatus(Model model) {
        List<User> contacts = model.getContactList();
        List<Message> messages = model.getMessages();
        String username = model.getCurrentUser().getUsername();
        JsonValue contacsJson = createContacts(username, contacts);
        JsonValue convesationsJson = createMessages(username, messages);
    }

    private static JsonValue createMessages(String username, List<Message> messages) {
        JsonObject json = getJsonObject(username);
        List<String> contactOfMessages = new ArrayList<>();
        for(Message m : messages){
            if(!contactOfMessages.contains(m.getRemitent()) && !m.getRemitent().equals(username)){
                contactOfMessages.add(m.getRemitent());
            }
            if(!contactOfMessages.contains(m.getDestinatary()) && !m.getDestinatary().equals(username)){
                contactOfMessages.add(m.getDestinatary());
            }
        }
        JsonValue conversations;
        jsonBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonAB = Json.createArrayBuilder();
        for(String s : contactOfMessages){
            for(Message m : messages){
                if(s.equals(m.getRemitent()) || s.equals(m.getDestinatary())){
                    jsonAB.add(Json.createObjectBuilder()
                            .add("message", m.getMessage())
                            .add("remitent", m.getRemitent())
                            .add("destinatary", m.getDestinatary())
                            .add("dateTime", m.getDateTime().toString()));
                }
            }
            jsonBuilder.add(s, jsonAB.build());
        }
        conversations = jsonBuilder.build();

        JsonObject conversationsTag = json.getJsonObject("conversations");

        JsonValue patch = Json.createObjectBuilder().add("conversations", conversations).build();
        JsonMergePatch jsonMergePatch = Json.createMergePatch(patch);
        JsonValue result = jsonMergePatch.apply(json);

        saveOnFile(username, result);
        return null;
    }

    private static JsonValue createContacts(String username, List<User> contacts) {
        JsonValue json = getJsonObject(username);
        json = json.asJsonObject().getJsonArray("contacts");
        List<String> contactsUsernames = new ArrayList<>();
        for(User u : contacts){
            contactsUsernames.add(u.getUsername());
        }
        for(JsonString js : json.asJsonArray().getValuesAs(JsonString.class)){
            if(!contactsUsernames.contains(js.getString())){
                contactsUsernames.add(js.getString());
            }
        }
        JsonValue jsonValue = Json.createObjectBuilder()
                .add("contacts", Json.createArrayBuilder(contactsUsernames).build()).build();
        return jsonValue;
    }
}