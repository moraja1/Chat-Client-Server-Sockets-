/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.presentation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Model {
    private User currentUser;
    private User userSelected;
    private HashMap<String, Message> messages;
    public Model() {
       currentUser = null;
       messages= new LinkedHashMap<>();
    }
    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    public HashMap<String, Message> getMessages() {
        return messages;
    }
    public void setMessages(HashMap<String, Message> messages) {
        this.messages = messages;
    }
    public User getUserSelected() {
        return userSelected;
    }
    public void setUserSelected(User userSelected) {
        this.userSelected = userSelected;
    }
}
