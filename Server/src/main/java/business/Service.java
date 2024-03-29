package business;

import data.Server;
import data.dao.MessageDAO;
import data.dto.MessageDetails;
import data.dto.UserDetails;
import data.model.Worker;
import data.util.Exceptions.LoginException;
import data.util.Exceptions.RegisterException;
import data.dao.DAO;
import data.dao.UserDAO;
import data.model.repository.Message;
import data.model.repository.User;
import data.util.ParserToJSON;
import data.util.Protocol;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Service {
    private List<User> usersLoggedIn = new ArrayList<>();
    private DAO dao;
    private Server server;
    public Service() {
    }
    public void login(ObjectInputStream input, ObjectOutputStream output) throws LoginException {
        String userJson = null;
        User user;
        try {
            //Read user info
            userJson = (String) input.readObject();
            user = ParserToJSON.JsonToUser(userJson);
            if(user != null){
                //Try to log in
                user = authenticate(user);
                userJson = ParserToJSON.UserToJson(user);
                output.writeInt(Protocol.ERROR_NO_ERROR);
                output.writeObject(userJson);
                output.flush();
                server.createWorker(input, output, user);
                server.newUserConnected(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LoginException();
        }
    }
    public void register(ObjectInputStream input, ObjectOutputStream output) throws RegisterException {
        String userJson = null;
        User user;
        try {
            //Read user info
            userJson = (String) input.readObject();
            System.out.println(userJson);
            user = ParserToJSON.JsonToUser(userJson);
            if(user != null){
                dao = new UserDAO();
                dao.add(user);
                user = authenticate(user);
                userJson = ParserToJSON.UserToJson(user);
                output.writeInt(Protocol.ERROR_NO_ERROR);
                output.writeObject(userJson);
                output.flush();
                server.createWorker(input, output, user);
            }
        } catch (Exception e) {
            throw new RegisterException();
        }
    }
    public User authenticate(User userInput) throws LoginException {
        UserDAO dao = new UserDAO();
        User user = dao.getSingleObject(userInput.getUsername(), userInput.getPassword());
        if(user != null){
            for(User u : usersLoggedIn){
                if(user.equals(u)) {
                    return user;
                }
            }
            usersLoggedIn.add(user);
            return user;
        } else{
            throw new LoginException("Usuario no registrado");
        }
    }
    public void logout(User user) {
        server.removeWorker(user);
        usersLoggedIn.remove(user);
    }
    public List<Message> getPendingMessages(User user) {
        MessageDAO dao = new MessageDAO();
        List<Message> messages = dao.getAllObjects();
        List<Message> pendingMessages = new ArrayList<>();
        for(Message m : messages){
            if(m.getDestinatary().getUsername().equals(user.getUsername())){
                pendingMessages.add(m);
            }
        }
        return pendingMessages;
    }
    public void messageDelivered(Message message) {
        dao = new MessageDAO();
        dao.erase(message);
    }
    public void messageDelivered(String messagesJson) {
        List<MessageDetails> messages = ParserToJSON.JsonToMessages(messagesJson);
        if(!messages.isEmpty()){
            UserDAO userDAO = new UserDAO();
            User destinatary = userDAO.getSingleObject(messages.get(0).getDestinatary());
            List<Message> messagesPersisted = getPendingMessages(destinatary);
            if(!messagesPersisted.isEmpty()){
                MessageDAO dao = new MessageDAO();
                for(Message me : messagesPersisted){
                    dao.erase(me);
                }
            }
        }
    }
    public void messageUndelivered(MessageDetails message) {
        UserDAO userDAO = new UserDAO();
        User remitent = userDAO.getSingleObject(message.getRemitent());
        User destinatary = userDAO.getSingleObject(message.getDestinatary());
        MessageDAO dao = new MessageDAO();
        Message messagePersisted = dao.getSingleObject(message.getMessage(), remitent, destinatary, Timestamp.valueOf(message.getDateTime()));
        if(messagePersisted == null){
            dao.add(new Message(message.getMessage(), Timestamp.valueOf(message.getDateTime()), remitent, destinatary));
        }
    }
    public List<User> getPersistedUsers(List<User> contactList) {
        List<User> persistedUsers = new ArrayList<>();
        UserDAO userDAO = new UserDAO();
        for(User u : contactList){
            persistedUsers.add(userDAO.getSingleObject(u.getUsername()));
        }
        return persistedUsers;
    }
    public void setServer(Server server) {
        this.server = server;
    }

    public void sendContactState(Worker worker, UserDetails contact) {
        for (User u : usersLoggedIn){
            if(u.getUsername().equals(contact.getUsername())){
                contact.setConnected(true);
            }
            String contactJson = ParserToJSON.contactToJson(contact);
            worker.sendContactState(contactJson);
        }
    }
    public void messageUndelivered(String pending) {
        MessageDetails messageDetails = ParserToJSON.JsonToMessage(pending);
        messageUndelivered(messageDetails);
    }

    public void searchContact(Worker worker, UserDetails contact) {
        UserDAO userDAO = new UserDAO();
        User u = userDAO.getSingleObject(contact.getUsername());
        if(u != null){
            UserDetails userDetails = new UserDetails(u.getUsername());
            if(usersLoggedIn.contains(u)){
                userDetails.setConnected(true);
            }else{
                userDetails.setConnected(false);
            }
            String user = ParserToJSON.contactToJson(userDetails);
            worker.getContacts().add(userDetails);
            worker.sendSearch(user);
        }else{
            worker.sendSearch("");
        }
    }
}
