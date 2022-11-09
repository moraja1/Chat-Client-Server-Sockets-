package business;

import data.util.Exceptions.LoginException;
import data.util.Exceptions.RegisterException;
import data.util.IService;
import data.dao.DAO;
import data.dao.UserDAO;
import data.model.repository.Message;
import data.model.repository.User;

import java.util.List;

public class Service implements IService {
    private List<User> usersLoggedIn;
    private DAO dao;
    public Service() {
    }
    
    public void post(Message m){
        // if wants to save messages, ex. recivier no logged on
    }

    public User login(User userInput) throws LoginException {
        UserDAO dao = new UserDAO();
        User user = dao.getSingleObject(userInput.getUsername(), userInput.getPassword());
        if(user != null){
            for(User u : usersLoggedIn){
                if(user.equals(u)) {
                    return u;
                }
            }
            usersLoggedIn.add(user);
            return user;
        } else{
            throw new LoginException("Usuario no registrado");
        }
    } 

    public void logout(User p) throws Exception{
        usersLoggedIn.remove(p);
    }

    @Override
    public User register(User u) throws RegisterException {
        return null;
    }
}
