package org.una.logic;

import jakarta.json.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class jsonFileAdmin {
    private static final String PATH = "src/main/resources/";
    public static String loadContacts(String username) {
        String fullPath = new StringBuilder().append(PATH).append(username).append(".json").toString();
        File contactsFile = new File(fullPath);
        if(!contactsFile.exists()){
            try {
                contactsFile.createNewFile();
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
                jsonBuilder.add("contacts", Json.createArrayBuilder().build());
                JsonObject json = jsonBuilder.build();
                OutputStream outStream = Files.newOutputStream(contactsFile.toPath());
                JsonWriter jsonWriter = Json.createWriter(outStream);
                jsonWriter.writeObject(json);
                jsonWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String fileAsJson;
        try {
            fileAsJson = new String(Files.readAllBytes(contactsFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileAsJson;
    }

    public static List<String> contactListFromJason(String contactsJson) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonReader jsonReader = Json.createReader(new StringReader(contactsJson));
        JsonObject json = jsonReader.readObject();
        JsonArray contacts = json.getJsonArray("contacts");
        List<String> contactList = new ArrayList<>();
        for(JsonString v : contacts.getValuesAs(JsonString.class)){
            contactList.add(v.getString());
        }
        return contactList;
    }
}
