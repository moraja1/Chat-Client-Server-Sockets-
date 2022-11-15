
package data;

import business.Service;
import data.dto.MessageDetails;
import data.model.repository.Message;
import data.model.repository.User;
import data.model.Worker;
import data.util.Exceptions.LoginException;
import data.util.Exceptions.OperationException;
import data.util.Exceptions.RegisterException;
import data.util.Protocol;
import business.controller.ServerWindow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<Worker> workers;
    private Service service;
    private boolean exit = false;
    public Server() throws Exception {
        try {
            serverSocket = new ServerSocket(Protocol.PORT);
        } catch (IOException e) {
            throw new Exception("No se logró habilitar el servidor");
        }
        workers =  Collections.synchronizedList(new ArrayList<Worker>());
        service = new Service();
        service.setServer(this);
        new ServerWindow(this);
    }
    
    public void run(){
        System.out.println("Servidor iniciado...");
        Socket socket = null;
        ObjectInputStream input=null;
        ObjectOutputStream output=null;

        while (!exit) {
            try {
                //*******************************************************Opens Socket
                socket = serverSocket.accept();
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream() );
                System.out.println("Conexion Establecida...");

                //********************************************************Execute operation
                executeOperation(input, output);

            }catch (LoginException | RegisterException e) {
                try {
                    if(output != null){
                        if(e.getClass() == LoginException.class){
                            output.writeInt(Protocol.ERROR_LOGIN);
                        }else{
                            output.writeInt(Protocol.ERROR_REGISTER);
                        }
                    }
                } catch (IOException ex) {}
            }catch (Exception operationException){
                try {
                    if(output != null){
                        output.writeInt(Protocol.ERROR_OPERATON);
                    }
                } catch (IOException ex) {}
            } finally {
                if(output != null){
                    try {
                        output.flush();
                    } catch (IOException e) {}
                }
            }
        }
        System.out.println("Servidor finalizado...");
    }
    private void executeOperation(ObjectInputStream input, ObjectOutputStream output) throws LoginException, RegisterException, OperationException {
        int method;
        try {
            method = input.readInt();
        } catch (Exception e) {
            throw new OperationException();
        }
        switch (method){
            case Protocol.LOGIN: //******************************LOGIN***********************
            {
                service.login(input, output);
                break;
            }
            case Protocol.REGISTER: //******************************REGISTER***********************
            {
                service.register(input, output);
                break;
            }
            default:
            {
                try {
                    output.writeInt(Protocol.ERROR_OPERATON);
                } catch (IOException e) {
                    throw new OperationException("Debe registrarse o iniciar sesión primero.");
                }
            }
        }
    }
    public void deliver(MessageDetails message){
        String destinatary = message.getDestinatary();
        Boolean delivered = false;
        for(Worker w : workers){
            if(w.getUser().getUsername().equals(destinatary)){
                w.deliver(message);
                delivered = true;
            }
        }
        if(!delivered){
            service.messageUndelivered(message);
        }
    }
    public void removeWorker(User u, List<User> contactList){
        for(Worker wk : workers){
            if(wk.getUser().equals(u)){
                workers.remove(wk);
            }
            if(contactList.contains(wk.getUser())){
                wk.sendLogoutMessage(u.getUsername());
            }
        }
        System.out.println("Quedan: " + workers.size());
    }
    public void createWorker(ObjectInputStream input, ObjectOutputStream output, User user){
        //Create Worker
        Worker worker = new Worker(this, input, output, user, service);
        //Add worker to memory and start run method.
        workers.add(worker);
    }
    public void close(){
        //Creo un nuevo hilo para cerrar el ServerSocket
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }).start();
        //Saco el sistema del bucle
        exit = true;
    }
}