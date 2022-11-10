package data.util;

import data.model.repository.Message;
import data.model.repository.User;
import data.util.Exceptions.LoginException;
import data.util.Exceptions.RegisterException;

import java.util.List;

public interface IService {
    User register(User u) throws RegisterException;
    User login(User u) throws LoginException;
    void logout(User u) throws Exception;
    void post(Message m);
    List<Message> getPendingMessages(User user);
}
