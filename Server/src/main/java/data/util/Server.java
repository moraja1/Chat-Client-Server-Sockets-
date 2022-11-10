
package data.util;

import business.Service;
import business.controller.ServerWindow;
import data.model.repository.Message;
import data.model.repository.User;
import data.model.Worker;
import data.util.Exceptions.LoginException;
import data.util.Exceptions.OperationException;
import data.util.Exceptions.RegisterException;

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
    private IService service;
    private boolean exit = false;
    public Server() throws Exception {
        try {
            serverSocket = new ServerSocket(Protocol.PORT);
        } catch (IOException e) {
            throw new Exception("No se logró habilitar el servidor");
        }
        workers =  Collections.synchronizedList(new ArrayList<Worker>());
        service = new Service();
        new ServerWindow(this);
    }
    
    public void run(){
        System.out.println("Servidor iniciado...");
        Socket socket = null;
        ObjectInputStream input=null;
        ObjectOutputStream output=null;

        while (!exit) {
            try {
                socket = serverSocket.accept();
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream() );
                System.out.println("Conexion Establecida...");
                //Execute operation
                User user = executeOperation(input, output);
                //Create Worker
                Worker worker = new Worker(this, input, output, user, service);
                //Create Thread
                Thread userThread = new Thread(worker);
                worker.setThreadParent(userThread);
                //Add worker to memory and start run method.
                workers.add(worker);
                userThread.start();
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
                        socket.close();
                    } catch (IOException e) {}
                }
                System.out.println("Conexion cerrada...");
            }
        }
        System.out.println("Servidor finalizado...");
    }
    private User executeOperation(ObjectInputStream input, ObjectOutputStream output) throws LoginException, RegisterException, OperationException {
        User user = null;
        int method;
        try {
            method = input.readInt();
        } catch (Exception e) {
            throw new OperationException();
        }
        switch (method){
            case Protocol.LOGIN:
            {
                user = login(input, output);
                break;
            }
            case Protocol.REGISTER:
            {
                user = register(input, output);
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
        return user;
    }
    private User login(ObjectInputStream input, ObjectOutputStream output) throws LoginException {
        String userJson = null;
        User user;
        try {
            userJson = (String) input.readObject();
            System.out.println(userJson);
            user = ParserToJSON.JsonToUser(userJson);
            System.out.println(user.getUsername());
            /*if(user != null){
                user = service.login(user);
                output.writeInt(Protocol.ERROR_NO_ERROR);
                output.writeObject(user);
                output.flush();
            }*/
        } catch (Exception e) {
            throw new LoginException();
        }
        return null;
    }
    private User register(ObjectInputStream input, ObjectOutputStream output) throws RegisterException {
        User user = null;
        try {
            user = (User) input.readObject();
            if(user != null){
                user = service.register(user);
                output.writeInt(Protocol.ERROR_NO_ERROR);
                output.writeObject(user);
                output.flush();
            }
        } catch (Exception e) {
            throw new RegisterException();
        }
        return user;
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