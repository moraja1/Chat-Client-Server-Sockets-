package org.una.logic;

import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;
import org.una.presentation.model.Message;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        for(String s : messages){
            System.out.println(s);
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
        System.out.println(messages);
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
}