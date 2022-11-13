package org.una.presentation.controller;

import org.una.Exceptions.LoginException;
import org.una.Exceptions.OperationException;
import org.una.logic.ServiceProxy;
import org.una.logic.jsonFileAdmin;
import org.una.presentation.view.ChatView;
import org.una.presentation.model.Message;
import org.una.presentation.model.Model;
import org.una.presentation.model.User;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Controller {
    private ChatView view;
    private Model model;
    private ServiceProxy localService;
    private List<String> contactsList;
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
    public void register(){

    }
    public void login(){
        try {
            User user = new User(view.getUsername().getText(), new String(view.getClave().getPassword()));
            User logged = localService.login(user);
            model.setCurrentUser(logged);
            view.loginAccepted(logged.getUsername());
            String contactsJson = jsonFileAdmin.loadContacts(user.getUsername());
            contactsList = jsonFileAdmin.contactListFromJason(contactsJson);
            view.setContactListValues(contactsList.toArray(new String[contactsList.size()]));
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
        localService.post(message);
        //model.commit(Model.CHAT);
    }
    public void logout(){
        try {
            ServiceProxy.getInstance().logout(model.getCurrentUser());
            //model.setMessages(new ArrayList<>());
            //model.commit(Model.CHAT);
        } catch (Exception ex) {
        }
        model.setCurrentUser(null);
        //model.commit(Model.USER+Model.CHAT);
    }
    public void deliver(Message message){
        model.getMessages().put(message.getRemitent(), message);
        updateMessages(model.getMessages());
    }
    public void updateMessages(HashMap<String, Message> messages) {
        //----------------------------Buscar el usuario seleccionado-----------------------------------------
        view.getMessages().setText("");
        String text = "";
        for (Message m : messages.values()) {
            if (m.getRemitent().equals(model.getCurrentUser())) {
                text += ("Me:" + m.getMessage() + "\n");
            } else {
                text += (m.getRemitent() + ": " + m.getMessage() + "\n");
            }
        }
        view.getMessages().setText(text);
    }
}
