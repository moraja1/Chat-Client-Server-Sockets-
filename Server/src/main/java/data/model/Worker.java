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
    private MessageDetails lastMessage;
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
        sendPendingMessages();
        listen();
    }

    private void sendPendingMessages() {
        //Send Pending Messages
        List<Message> pendingMessages = service.getPendingMessages(user);
        List<MessageDetails> pendingMessagesToSend = new ArrayList<>();

        if(!pendingMessages.isEmpty()) {
            for(Message m : pendingMessages){
                pendingMessagesToSend.add(new MessageDetails(m));
            }
            for(MessageDetails md : pendingMessagesToSend){
                server.deliver(md);
            }
        }
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
                        if(needsConfirmation){
                            saveLastMessage();
                        }
                        break;
                    case Protocol.CONTACT_DELIVER:
                        if(needsConfirmation){
                            messageConfirmed();
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
                    default:
                        break;
                }
                output.flush();
            } catch (IOException  ex) {
                continuar = false;
            }                        
        }
    }
    private void messageConfirmed() {
        service.messageDelivered(lastMessage);
        lastMessage = null;
        needsConfirmation = false;
    }
    private void saveLastMessage() {
        service.messageUndelivered(lastMessage);
        messageConfirmed();
    }

    public void deliver(MessageDetails message){
        lastMessage = message;
        needsConfirmation = true;
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
}
