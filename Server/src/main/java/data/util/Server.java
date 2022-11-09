
package data.util;

import business.IService;
import business.Protocol;
import business.Service;
import data.model.Message;
import data.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private ServerSocket srv;
    private List<Worker> workers;
    private IService service;
    public Server() throws Exception {
        try {
            srv = new ServerSocket(Protocol.PORT);
        } catch (IOException e) {
            throw new Exception("No se logró habilitar el servidor");
        }
        workers =  Collections.synchronizedList(new ArrayList<Worker>());
        service = new Service();
        System.out.println("Servidor iniciado...");
    }
    
    public void run(){
        ObjectInputStream in=null;
        ObjectOutputStream out=null;
        Socket skt=null;

        while (true) {
            try {
                skt = srv.accept();
                in = new ObjectInputStream(skt.getInputStream());
                out = new ObjectOutputStream(skt.getOutputStream() );
                System.out.println("Conexion Establecida...");

                //Loggin in
                User user = this.login(in, out, service);
                //Create Worker
                Worker worker = new Worker(this, in, out, user, service);
                workers.add(worker);
                //Create Thread
                Thread userThread = new Thread(worker);
                userThread.start();
            }catch (Exception e) {
                try {
                    out.writeInt(Protocol.ERROR_LOGIN);
                    out.flush();
                    skt.close();
                } catch (IOException ex) {}
               System.out.println("Conexion cerrada...");
            }
        }
    }
    
    private User login(ObjectInputStream in, ObjectOutputStream out, IService service) throws IOException, ClassNotFoundException, Exception{
        int method = in.readInt();
        if (method != Protocol.LOGIN) throw new Exception("Should login first");
        User user = (User)in.readObject();
        try{
            user = service.login(user);
            out.writeInt(Protocol.ERROR_NO_ERROR);
            out.writeObject(user);
            out.flush();
            return user;
        }catch (Exception e){
            throw e;
        }
    }
    
    public void deliver(Message message){
        for(Worker wk:workers){
            wk.deliver(message);
        }        
    } 
    
    public void remove(User u){
        for(Worker wk : workers){
            if(wk.getUser().equals(u)){
                workers.remove(wk);
                break;
            }
        }
        System.out.println("Quedan: " + workers.size());
    }
    
}