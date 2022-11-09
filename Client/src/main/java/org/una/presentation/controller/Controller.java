package org.una.presentation.controller;

import org.una.logic.ServiceProxy;
import org.una.presentation.model.Message;
import org.una.presentation.model.Model;
import org.una.presentation.model.User;
import org.una.presentation.view.View;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {
    private View view;
    private Model model;
    private ServiceProxy localService;
    public Controller(View view, Model model) {
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
            model.commit(Model.USER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudo iniciar sessión", "El usuario ingresado no existe", JOptionPane.WARNING_MESSAGE);;
        }
    }
    public void post(String text){
        Message message = new Message();
        message.setMessage(text);
        message.setRemitent(model.getCurrentUser());
        localService.post(message);
        model.commit(Model.CHAT);
    }
    public void logout(){
        try {
            ServiceProxy.getInstance().logout(model.getCurrentUser());
            model.setMessages(new ArrayList<>());
            model.commit(Model.CHAT);
        } catch (Exception ex) {
        }
        model.setCurrentUser(null);
        model.commit(Model.USER+Model.CHAT);
    }
    public void deliver(Message message){
        model.getMessages().add(message);
        model.commit(Model.CHAT);       
    }
    @Override
    public void update(Observable o, Object arg) {
        int prop = (int) arg;

        if (model.getCurrentUser() == null) {
            view.setTitle("CHAT");
            view.getLoginPanel().setVisible(true);
            view.getBodyPanel().setVisible(false);
        } else {
            view.setTitle(model.getCurrentUser().getUsername().toUpperCase());
            view.getLoginPanel().setVisible(false);;
            view.getBodyPanel().setVisible(true);
            view.getRootPane().setDefaultButton(view.getPostButton());
            if ((prop & Model.CHAT) == Model.CHAT) {
                view.getMessages().setText("");
                String text = "";
                for (Message m : model.getMessages()) {
                    if (m.getRemitent().equals(model.getCurrentUser())) {
                        text += ("Me:" + m.getMessage() + "\n");
                    } else {
                        text += (m.getRemitent().getUsername() + ": " + m.getMessage() + "\n");
                    }
                }
                view.getMessages().setText(text);
            }
        }
        view.getPanel().validate();
    }
}
