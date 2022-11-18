package data.model;

import business.Service;
import data.dto.MessageDetails;
import data.dto.UserDetails;
import data.model.repository.Message;
import data.model.repository.User;
import data.util.ParserToJSON;
import data.util.Protocol;
import data.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Worker implements Runnable{
    private Server server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Service service;
    private User user;
    boolean continuar = true;
    private Thread threadParent;
    private String pending;
    private boolean needsConfirmation = false;
    private boolean messageConfirmed = false;
    private boolean contactConfirmation = false;
    public Worker(Server server, ObjectInputStream input, ObjectOutputStream output, User user, Service service) {
        this.server = server;
        this.input = input;
        this.output = output;
        this.user=user;
        this.service=service;
        threadParent = new Thread(this, user.getUsername());
        threadParent.start();
    }
    @Override
    public void run() {
        listen();
    }
    public void listen(){
        int method;
        String messageJson;

        while (continuar) {
            try {
                method = input.readInt();
                System.out.println("Operacion: "+ method);
                switch(method){
                    case Protocol.LOGOUT:
                        if(needsConfirmation){
                            saveLastMessage();
                        }
                        try {
                            String contactListJson = (String) input.readObject();
                            List<User> contactList = ParserToJSON.JsonToUsers(contactListJson);
                            contactList = service.getPersistedUsers(contactList);
                            service.logout(user, contactList); //Remove user from loggedin Users*/
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        stop();
                        break;
                    case Protocol.POST:
                        if(needsConfirmation){
                            saveLastMessage();
                        }
                        messageJson = null;
                        try {
                            messageJson = (String) input.readObject();
                            MessageDetails message = ParserToJSON.JsonToMessage(messageJson);
                            server.deliver(message);
                            System.out.println(user.getUsername() + ": " + message.getMessage());
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case Protocol.ERROR_NO_ERROR:
                        messageConfirmed();
                        break;
                    case Protocol.CONTACT_DELIVER:
                        if(needsConfirmation){
                            saveLastMessage();
                        }
                        messageJson = null;
                        try {
                            messageJson = (String) input.readObject();
                            UserDetails contact = ParserToJSON.JsonToContact(messageJson);
                            service.sendContactState(this, contact);
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case Protocol.PENDINGS:
                        sendPendingMessages();
                        break;
                    case Protocol.SEARCH:
                        try {
                            messageJson = (String) input.readObject();
                            UserDetails contact = ParserToJSON.JsonToContact(messageJson);
                            service.searchContact(this, contact);
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                output.flush();
            } catch (IOException  ex) {
                saveLastMessage();
                continuar = false;
            }                        
        }
    }
    private void messageConfirmed() {
        service.messageDelivered(pending);
        pending = null;
        needsConfirmation = false;
    }
    private void saveLastMessage() {
        if(pending != null){
            service.messageUndelivered(pending);
        }
        pending = null;
        needsConfirmation = false;
    }
    private void sendPendingMessages() {
        //Send Pending Messages
        List<Message> pendingMessages = service.getPendingMessages(user);
        if(!pendingMessages.isEmpty()){
            List<MessageDetails> messages = new ArrayList<>();
            for(Message m : pendingMessages){
                messages.add(new MessageDetails(m));
            }
            String messagesJson = ParserToJSON.PendingMessagesToJson(messages);
            pending = messagesJson;
            try{
                output.writeInt(Protocol.PENDINGS);
                output.writeObject(messagesJson);
                output.flush();
            }catch (Exception e){}
        }
    }
    public void deliver(MessageDetails message){
        try {
            String messageJson = ParserToJSON.MessageToJson(message);
            output.writeInt(Protocol.DELIVER);
            output.writeObject(messageJson);
            output.flush();
        } catch (Exception ex) {
            service.messageUndelivered(message);
        }
    }
    public void sendLogoutMessage(String username){
        try {
            output.writeInt(Protocol.LOGOUT);
            output.writeObject(username);
            output.flush();
        } catch (Exception ex) {}
    }
    public void stop(){
        continuar=false;
        System.out.println("Conexion cerrada...");
        threadParent.interrupt();
    }
    public User getUser() {
        return user;
    }
    public void sendContactState(String contactJson) {
        try {
            output.writeInt(Protocol.CONTACT_DELIVER);
            output.writeObject(contactJson);
            output.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void sendLoginMessage(String username) {
        try {
            output.writeInt(Protocol.LOGIN);
            output.writeObject(username);
            output.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void setPending(String pending) {
        this.pending = pending;
    }
    public void setNeedsConfirmation(boolean needsConfirmation) {
        this.needsConfirmation = needsConfirmation;
    }
    public void sendSearch(String userToJson) {
        try {
            output.writeInt(Protocol.SEARCH);
            output.writeObject(userToJson);
            output.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
