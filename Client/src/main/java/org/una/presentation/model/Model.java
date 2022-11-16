/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.presentation.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private User currentUser;
    private User userSelected;
    private List<Message> messages;
    private List<User> contactList;
    public Model() {
       currentUser = null;
       messages = new ArrayList<>();
       contactList = new ArrayList<>();
    }
    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    public User getUserSelected() {
        return userSelected;
    }
    public void setUserSelected(User userSelected) {
        this.userSelected = userSelected;
    }
    public List<User> getContactList() {
        return contactList;
    }
    public void setContactList(List<User> contactList) {
        this.contactList = contactList;
    }
}
