package data.model;

import business.Service;
import data.dto.MessageDetails;
import data.model.repository.Message;
import data.model.repository.User;
import data.util.ParserToJSON;
import data.util.Protocol;
import data.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
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
        String pendingMessagesJson = "";

        if(!pendingMessages.isEmpty()) {
            for(Message m : pendingMessages){
                pendingMessagesToSend.add(new MessageDetails(m));
            }
            pendingMessagesJson = ParserToJSON.PendingMessagesToJson(pendingMessagesToSend);
            try {
                output.writeInt(Protocol.DELIVER_COLLECTION);
                output.writeObject(pendingMessagesJson);
                output.flush();
                waitForResponse();
                for(Message m : pendingMessages){
                    service.messageDelivered(m);
                }
            }catch (Exception e){}
        }
    }
    public void listen(){
        int method;

        while (continuar) {
            try {
                method = input.readInt();
                System.out.println("Operacion: "+ method);
                switch(method){
                    case Protocol.LOGOUT:
                        try {
                            String userListJson = (String) input.readObject();
                            List<User> contactList = ParserToJSON.JsonToUsers(userListJson);
                            contactList = service.getPersistedUsers(contactList);
                            service.logout(user, contactList); //Remove user from loggedin Users
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        stop();
                        break;
                    case Protocol.POST:
                        String messageJson=null;
                        try {
                            messageJson = (String) input.readObject();
                            MessageDetails message = ParserToJSON.JsonToMessage(messageJson);
                            server.deliver(message);
                            System.out.println(user.getUsername() +": " + message.getMessage());
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
                ex.printStackTrace();
            }                        
        }
    }

    public void deliver(MessageDetails message){
        try {
            String messageJson = ParserToJSON.MessageToJson(message);
            output.writeInt(Protocol.DELIVER);
            output.writeObject(messageJson);
            output.flush();
            waitForResponse();
        } catch (Exception ex) {
            service.messageUndelivered(message);
        }
    }
    private void waitForResponse() throws RuntimeException, InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean confirmed = false;
                long startTime = System.currentTimeMillis();
                long currentTime = startTime;
                while(currentTime < startTime+10000 && !confirmed){
                    currentTime = System.currentTimeMillis();

                    try {
                        int response = input.readInt();
                        if (response == Protocol.ERROR_NO_ERROR) {
                            confirmed = true;
                        }
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
                if(!confirmed){
                    throw new RuntimeException();
                }
            }
        });
        t.start();
        t.join();
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
}
