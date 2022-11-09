package org.una.logic;

import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.una.logic.dto.ParserToJSON;
import org.una.presentation.controller.Controller;
import org.una.presentation.model.Message;
import org.una.presentation.model.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServiceProxy implements IService{
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Controller controller;
    private static IService theInstance;
    private Socket socket = null;

    private ServiceProxy() {
    }
    public static IService getInstance() throws Exception {
        if (theInstance==null){ 
            theInstance = new ServiceProxy();
        }
        return theInstance;
    }
    public User login(User u) throws Exception {
        if(socket == null){
            connect();
        }
        JsonObject user = ParserToJSON.UserToJson(u);
        try {
            output.writeInt(Protocol.LOGIN);
            output.writeObject(user.toString());
            output.flush();
            /*int response = input.read();
            if (response==Protocol.ERROR_NO_ERROR){
                User u1=(User) input.read();
                this.start();
                return u1;
            }
            else {
                disconnect();
                throw new Exception("No remote user");
            }*/
        } catch (IOException /*| ClassNotFoundException*/ ex) {
            return null;
        }
            return null;
    }
    @Override
    public void register(User u) throws Exception {
        if(socket == null){
            connect();
        }
    }
    private void connect() throws Exception{
        socket = new Socket(Protocol.SERVER, Protocol.PORT);
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
    }
    private void disconnect() throws IOException {
        socket.shutdownOutput();
        socket.close();
    }

    public void logout(User u) throws IOException {
        output.write(Protocol.LOGOUT);
        /*output.write(u);
        output.flush();
        this.stop();
        this.disconnect();*/
    }
    
    public void post(Message message){
        /*try {
            output.writeInt(Protocol.POST);
            output.writeObject(message);
            output.flush();
        } catch (IOException ex) {
            
        }   */
    }  

    // LISTENING FUNCTIONS
   boolean continuar = true;    
   public void start(){
        System.out.println("Client worker atendiendo peticiones...");
        Thread t = new Thread(new Runnable(){
            public void run(){
                listen();
            }
        });
        continuar = true;
        t.start();
    }
    public void stop(){
        continuar=false;
    }
    
   public void listen(){
        int method;
        while (continuar) {
            try {
                method = input.read();
                System.out.println("DELIVERY");
                System.out.println("Operacion: "+method);
                switch(method){
                case Protocol.DELIVER:
                    try {
                        Message message = (Message) input.readObject();
                        deliver(message);
                    } catch (ClassNotFoundException ex) {}
                    break;
                }
                output.flush();
            } catch (IOException  ex) {
                continuar = false;
            }                        
        }
    }
   private void deliver( final Message message ){
      SwingUtilities.invokeLater(new Runnable(){
            public void run(){
               controller.deliver(message);
            }
         }
      );
   }
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
