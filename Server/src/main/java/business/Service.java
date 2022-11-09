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

    public User login(User p) throws LoginException {
        for(User u : usersLoggedIn){
            if(p.equals(u)) {
                return u;
            }
        }
        dao = new UserDAO();
        if(dao.exists(p.getIdUser())){
            usersLoggedIn.add(p);
            return p;
        }else{
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
