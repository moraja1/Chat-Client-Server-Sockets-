package org.una.presentation.controller;

import org.una.Exceptions.LoginException;
import org.una.Exceptions.OperationException;
import org.una.Exceptions.RegisterException;
import org.una.logic.ServiceProxy;
import org.una.logic.dto.ParserToJSON;
import org.una.logic.jsonFileAdmin;
import org.una.presentation.view.ChatView;
import org.una.presentation.model.Message;
import org.una.presentation.model.Model;
import org.una.presentation.model.User;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Controller {
    private ChatView view;
    private Model model;
    private ServiceProxy localService;
    public Controller(ChatView view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.initComponents();
        try{
            localService = (ServiceProxy)ServiceProxy.getInstance();
            localService.setController(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initWindow(User logged){
        model.setCurrentUser(logged);
        view.loginAccepted(logged.getUsername());
        List<String> contactsUsernames = jsonFileAdmin.contactListFromJason(logged.getUsername());
        List<User> contactsList = new ArrayList<>();
        for(String s : contactsUsernames){
            contactsList.add(new User(s));
        }
        if(!contactsList.isEmpty()){
            view.setContactListValues(contactsList);
            model.setContactList(contactsList);
        }
        List<Message> messages = jsonFileAdmin.getAllConversations(logged.getUsername());
        if(!messages.isEmpty()){
            model.setMessages(messages);
        }
    }
    public void register(){
        try{
            User user = new User(view.getUsername().getText(), new String(view.getClave().getPassword()));
            User logged = localService.register(user);
            initWindow(logged);
        }catch (RegisterException e){
            JOptionPane.showMessageDialog(null, "Puede que ya se encuentre un usuario registrado con esa información", "No se pudo registrar", JOptionPane.WARNING_MESSAGE);
        }catch (OperationException | IOException | ClassNotFoundException ex){
            JOptionPane.showMessageDialog(null, "Existe un problema de conexión", "No se pudo iniciar sessión", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public void login(){
        try {
            User user = new User(view.getUsername().getText(), new String(view.getClave().getPassword()));
            User logged = localService.login(user);
            initWindow(logged);
            if(!model.getContactList().isEmpty()){
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        localService.askContactState(model.getContactList());
                        localService.askPendingMessages();
                    }
                });
            }
        } catch (LoginException e) {
            JOptionPane.showMessageDialog(null, "El usuario ingresado no existe", "No se pudo iniciar sessión", JOptionPane.WARNING_MESSAGE);
        } catch (OperationException | IOException | ClassNotFoundException ex){
            JOptionPane.showMessageDialog(null, "Existe un problema de conexión", "No se pudo iniciar sessión", JOptionPane.WARNING_MESSAGE);
        }
    }
    public void post(String text){
        Message message = new Message();
        message.setMessage(text);
        message.setRemitent(model.getCurrentUser().getUsername());
        message.setDestinatary(model.getUserSelected().getUsername());
        message.setDateTime(LocalDateTime.now());
        model.getMessages().add(message);
        localService.post(message);
        List<Message> messages = new ArrayList<>();
        partnerSelected(model.getUserSelected());
    }
    public void logout(){
        if(model.getCurrentUser() != null){
            jsonFileAdmin.saveCurrentStatus(model);
            try {
            localService.logout(model.getCurrentUser());
            model.setMessages(new ArrayList<>());
            model.setCurrentUser(null);
            model.setUserSelected(null);
            model.setContactList(null);
            view.logoutExecuted();
            } catch (Exception ex) {}
            model.setCurrentUser(null);
        }
    }
    public void deliver(Message message){
        model.getMessages().add(message);
        List<String> usernames = new ArrayList<>();
        for(User u : model.getContactList()){
            usernames.add(u.getUsername());
        }
        if(!usernames.contains(message.getRemitent())){
            model.getContactList().add(new User(message.getRemitent()));
            view.setContactListValues(model.getContactList());
            jsonFileAdmin.addNewContact(model.getCurrentUser().getUsername(), message.getRemitent());
        }
    }
    public void updateMessages(List<Message> messages) {
        //----------------------------Buscar el usuario seleccionado-----------------------------------------
        String selectedUser = model.getUserSelected().getUsername();
        String currentUser = model.getCurrentUser().getUsername();

        view.getMessages().setText("");
        String text = "";

        //----------------------------ESCRIBO EN PANTALLA----------------------------
        for(Message m : messages){
            if(selectedUser.equals(m.getRemitent()) && currentUser.equals(m.getDestinatary())){
                text += selectedUser + ": " + m.getMessage() + "\n";
            } else if (currentUser.equals(m.getRemitent()) && selectedUser.equals(m.getDestinatary())) {
                text += currentUser + ": " + m.getMessage() + "\n";
            }
        }
        view.getMessages().setText(text);
    }

    public void partnerSelected(User userSelected) {
        String uName = userSelected.getUsername();

        model.setUserSelected(userSelected);
        List<String> messagesByUser = jsonFileAdmin.getConversationWith(model.getCurrentUser().getUsername(), uName);
        List<Message> messages = new ArrayList<>();
        for(Message m : model.getMessages()){
            if(uName.equals(m.getRemitent()) || uName.equals(m.getDestinatary())){
                messages.add(m);
            }
        }
        updateMessages(messages);
    }

    public void notifyLogoutUser(String username) {
        JOptionPane.showMessageDialog(null, username + " ha cerrado la sesión", "Información", JOptionPane.INFORMATION_MESSAGE);
        for (User u : model.getContactList()){
            if (u.getUsername().equals(username)){
                u.setConected(false);
                break;
            }
        }
        printContacts(model.getContactList());
    }
    public void updateContacts(String contactJson) {
        User contact = ParserToJSON.JsonToUser(contactJson);
        for(User u : model.getContactList()) {
            if (u.getUsername().equals(contact.getUsername())) {
                if (u.isConected() != contact.isConected()) {
                    u.setConected(contact.isConected());
                }
            }
        }
        printContacts(model.getContactList());
    }
    private void printContacts(List<User> contactList) {
        if(!contactList.isEmpty()){
            view.setContactListValues(contactList);
        }
    }
    public void notifyLoginUser(String username) {
        JOptionPane.showMessageDialog(null, username + " ha iniciado la sesión", "Información", JOptionPane.INFORMATION_MESSAGE);
        for (User u : model.getContactList()){
            if (u.getUsername().equals(username)){
                u.setConected(true);
                break;
            }
        }
        printContacts(model.getContactList());
    }
}
