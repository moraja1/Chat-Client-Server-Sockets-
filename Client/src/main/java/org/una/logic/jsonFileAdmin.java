package org.una.logic;

import jakarta.json.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class jsonFileAdmin {
    private static final String PATH = "src/main/resources/";
    private static JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    private static File jsonFile;
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
        JsonObject conversations = json.getJsonObject("conversations");
        JsonArray messagesJson = conversations.getJsonArray(contactUsername);
        List<String> messages = new ArrayList<>();
        if(messagesJson == null){
            //Creates a space for contact messages
            JsonValue patch = Json.createObjectBuilder().add("conversations", Json.createObjectBuilder().add(contactUsername, Json.createArrayBuilder().build())).build();
            JsonMergePatch jsonMergePatch = Json.createMergePatch(patch);
            JsonValue result = jsonMergePatch.apply(json);
            //Save it on memory
            saveOnFile(username, result);
        }else{
            for(JsonString message : messagesJson.getValuesAs(JsonString.class)){
                messages.add(message.getString());
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
        try{
            OutputStream outStream = Files.newOutputStream(jsonFile.toPath());
            JsonWriter jsonWriter = Json.createWriter(outStream);
            jsonWriter.writeObject(result.asJsonObject());
            jsonWriter.close();
        }catch (Exception e){}
    }
}
