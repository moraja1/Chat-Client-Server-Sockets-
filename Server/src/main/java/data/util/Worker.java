package data.util;

import business.IService;
import business.Protocol;
import data.model.Message;
import data.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Worker implements Runnable{
    private Server srv;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private IService service;
    private User user;
    boolean continuar = true;
    public Worker(Server srv, ObjectInputStream in, ObjectOutputStream out, User user, IService service) {
        this.srv=srv;
        this.in=in;
        this.out=out;
        this.user=user;
        this.service=service;
    }

    @Override
    public void run() {
        listen();
    }
    public void deliver(Message message){
        try {
            out.writeInt(Protocol.DELIVER);
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {
        }
    }
    public void listen(){
        int method;

        while (continuar) {
            try {
                method = in.readInt();
                System.out.println("Operacion: "+ method);
                switch(method){
                    //case Protocol.LOGIN: done on accept
                    case Protocol.LOGOUT:
                        try {
                            srv.remove(user);
                            service.logout(user); //Remove user from loggedin Users
                        } catch (Exception ex) {}
                        stop();
                        break;
                    case Protocol.POST:
                        Message message=null;
                        try {
                            message = (Message)in.readObject();
                            message.setRemitent(user);
                            srv.deliver(message);
                            //service.post(message); // if wants to save messages, ex. recivier no logged on
                            System.out.println(user.getUsername()+": "+message.getMessage());
                        } catch (ClassNotFoundException ex) {}
                        break;
                    default:
                        break;
                }
                out.flush();
            } catch (IOException  ex) {
                System.out.println(ex);
                continuar = false;
            }                        
        }
    }
    public void stop(){
        continuar=false;
        System.out.println("Conexion cerrada...");
    }
    public User getUser() {
        return user;
    }


}
