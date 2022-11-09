package business;

import data.model.Message;
import data.model.User;

public interface IService {
    public User login(User u) throws Exception;
    public void logout(User u) throws Exception; 
    public void post(Message m);
}
