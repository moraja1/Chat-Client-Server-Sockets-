package org.una.logic;

import org.una.Exceptions.LoginException;
import org.una.Exceptions.OperationException;
import org.una.Exceptions.RegisterException;
import org.una.logic.dto.ParserToJSON;
import org.una.presentation.controller.Controller;
import org.una.presentation.model.Message;
import org.una.presentation.model.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServiceProxy implements IService{
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Controller controller;
    private static IService theInstance;
    private Socket socket = null;
    boolean continuar = true;

    private ServiceProxy() {
    }
    public static IService getInstance() throws Exception {
        if (theInstance==null){
            theInstance = new ServiceProxy();
        }
        return theInstance;
    }
    @Override
    public User login(User u) throws LoginException, OperationException, IOException, ClassNotFoundException {
        connect();
        String userJson = ParserToJSON.UserToJson(u);
        try {
            output.writeInt(Protocol.LOGIN);
            output.writeObject(userJson);
            output.flush();
            int response = input.readInt();
            if (response == Protocol.ERROR_NO_ERROR){
                String userResponse = (String) input.readObject();
                User user = ParserToJSON.JsonToUser(userResponse);
                this.start();
                return user;
            } else if (response == Protocol.ERROR_LOGIN) {
                throw new LoginException();
            } else {
                throw new OperationException();
            }
        } catch (IOException ex) {
            throw new OperationException();
        }
    }
    @Override
    public void logout(User u) throws IOException {
        String contactListJson = jsonFileAdmin.getLogoutContactList(u.getUsername());
        output.writeInt(Protocol.LOGOUT);
        output.writeObject(contactListJson);
        output.flush();
        this.stop();
        this.disconnect();
    }
    @Override
    public User register(User u) throws RegisterException, OperationException, IOException, ClassNotFoundException  {
        connect();
        String userJson = ParserToJSON.UserToJson(u);
        try {
            output.writeInt(Protocol.REGISTER);
            output.writeObject(userJson);
            output.flush();
            int response = input.readInt();
            if (response == Protocol.ERROR_NO_ERROR){
                String userResponse = (String) input.readObject();
                User user = ParserToJSON.JsonToUser(userResponse);
                this.start();
                return user;
            } else if (response == Protocol.ERROR_LOGIN) {
                throw new RegisterException();
            } else {
                throw new OperationException();
            }
        } catch (IOException ex) {
            throw new OperationException();
        }
    }
    @Override
    public void post(Message message){
        String messageJson = ParserToJSON.MessageToJson(message);
        System.out.println("POST");
        System.out.println("Operacion:" + Protocol.POST);
        try {
            output.writeInt(Protocol.POST);
            output.writeObject(messageJson);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void deliver( final Message message ) throws IOException {
        SwingUtilities.invokeLater(new Runnable(){
               public void run(){
                   controller.deliver(message);
               }
           }
        );
    }
    //-----------------------------------------------------------------------------------------------------------------

    //OPERATIONAL FUNCTIONS
    private void connect() throws IOException{
        socket = new Socket(Protocol.SERVER, Protocol.PORT);
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
    }

    private void start(){
        System.out.println("Client worker atendiendo peticiones...");
        Thread t = new Thread(new Runnable(){
            public void run(){
                listen();
            }
        });
        continuar = true;
        t.start();
    }
    private void disconnect() throws IOException {
        socket.shutdownOutput();
        socket.close();
        socket = null;
    }
    private void stop(){
        continuar=false;
    }
    //-----------------------------------------------------------------------------------------------------------------

    // LISTENING FUNCTIONS
    private void listen(){
        int method;
        while (continuar) {
            try {
                method = input.readInt();
                System.out.println("DELIVERY");
                System.out.println("Operacion: " + method);
                switch(method){
                    case Protocol.RECEIVE: {
                        try {
                            String messageJson = (String) input.readObject();
                            if(!messageJson.isEmpty()){
                                Message message = ParserToJSON.JsonToMessage(messageJson);
                                deliver(message);
                            }
                        } catch (ClassNotFoundException ex) {
                            output.writeInt(Protocol.ERROR_OPERATON);
                            output.flush();
                            break;
                        }
                        output.writeInt(Protocol.ERROR_NO_ERROR);
                        output.flush();
                        break;
                    }
                    case Protocol.RECEIVE_COLLECTION: {
                        try {
                            String messagesJson = (String) input.readObject();
                            if(!messagesJson.isEmpty()){
                                List<Message> messages = ParserToJSON.JsonToMessageList(messagesJson);
                                for(Message message : messages){
                                    deliver(message);
                                }
                            }
                        } catch (ClassNotFoundException ex) {
                            output.writeInt(Protocol.ERROR_OPERATON);
                            output.flush();
                            break;
                        }
                        output.writeInt(Protocol.ERROR_NO_ERROR);
                        output.flush();
                        break;
                    }
                    case Protocol.LOGOUT: {
                        try{
                            String messagesJson = (String) input.readObject();
                            controller.notifyLogoutUser(messagesJson);
                        }catch (ClassNotFoundException ex){
                            output.writeInt(Protocol.ERROR_OPERATON);
                            output.flush();
                            break;
                        }
                    }
                    case Protocol.CONTACT_DELIVER: {
                        try{
                            String contactJson = (String) input.readObject();
                            controller.updateContacts(contactJson);
                        }catch (ClassNotFoundException ex){
                            output.writeInt(Protocol.ERROR_OPERATON);
                            output.flush();
                            break;
                        }
                    }
                    case Protocol.LOGIN: {
                        try{
                            String username = (String) input.readObject();
                            controller.notifyLoginUser(username);
                        }catch (ClassNotFoundException ex){
                            output.writeInt(Protocol.ERROR_OPERATON);
                            output.flush();
                            break;
                        }
                    }
                }
                if(output != null){
                    output.flush();
                }
            } catch (IOException  ex) {}
        }
    }
   //------------------------------------------------------------------------------------------------------------------

   //MEMBER FUNCTIONS
   public void setController(Controller controller) {
        this.controller = controller;
    }
    public void askContactState(List<User> contactsList) {
        try {
            for (User u : contactsList){
                String userJson = ParserToJSON.UserToJson(u);
                output.writeInt(Protocol.CONTACT_DELIVER);
                output.writeObject(userJson);
                output.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}