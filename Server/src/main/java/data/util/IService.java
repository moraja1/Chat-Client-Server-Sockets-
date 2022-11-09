package data.util;

import data.model.repository.Message;
import data.model.repository.User;
import data.util.Exceptions.LoginException;
import data.util.Exceptions.RegisterException;

public interface IService {
    public User register(User u) throws RegisterException;
    public User login(User u) throws LoginException;
    public void logout(User u) throws Exception; 
    public void post(Message m);
}
