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
import java.util.HashMap;
import java.util.List;

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
    public void register(){
        try{
            User user = new User(view.getUsername().getText(), new String(view.getClave().getPassword()));
            User logged = localService.register(user);
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
            model.setCurrentUser(logged);
            view.loginAccepted(logged.getUsername());
            List<String> contactsList = jsonFileAdmin.contactListFromJason(user.getUsername());
            if(!contactsList.isEmpty()){
                view.setContactListValues(contactsList.toArray(new String[contactsList.size()]));
                model.setContactList(contactsList);
            }
            List<Message> messages = jsonFileAdmin.getAllConversations(logged.getUsername());
            if(!messages.isEmpty()){
                model.setMessages(messages);
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
        updateMessages();
        jsonFileAdmin.addNewMessage(model.getCurrentUser().getUsername(), model.getUserSelected().getUsername(), message);
    }
    public void logout(){
        try {
            localService.logout(model.getCurrentUser());
            //model.setMessages(new ArrayList<>());
            //model.commit(Model.CHAT);
        } catch (Exception ex) {
        }
        model.setCurrentUser(null);
        //model.commit(Model.USER+Model.CHAT);
    }
    public void deliver(Message message){
        model.getMessages().add(message);
        jsonFileAdmin.addNewMessage(model.getCurrentUser().getUsername(), message.getRemitent(), message);
        if(!model.getContactList().contains(message.getRemitent())){
            model.getContactList().add(message.getRemitent());
            view.setContactListValues(model.getContactList().toArray(new String[model.getContactList().size()]));
            jsonFileAdmin.addNewContact(model.getCurrentUser().getUsername(), message.getRemitent());
        }
        updateMessages();
    }
    public void updateMessages() {
        //----------------------------Buscar el usuario seleccionado-----------------------------------------
        String selectedUser = model.getUserSelected().getUsername();
        String currentUser = model.getCurrentUser().getUsername();

        view.getMessages().setText("");
        String text = "";
        List<Message> messages = model.getMessages();

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

    public void partnerSelected(String contactUsername) {
        model.setUserSelected(new User(contactUsername));
        List<String> messagesByUser = jsonFileAdmin.getConversationWith(model.getCurrentUser().getUsername(), contactUsername);
        List<Message> messages = new ArrayList<>();
        for(String s : messagesByUser){
            messages.add(ParserToJSON.JsonToMessage(s));
        }
        model.setMessages(new ArrayList<>());
        for(Message m : messages){
            model.getMessages().add(m);
        }
        updateMessages();
    }

    public void notifyLogoutUser(String messagesJson) {
        JOptionPane.showMessageDialog(null, messagesJson + " ha cerrado la sesión", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
