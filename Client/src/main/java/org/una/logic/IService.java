package org.una.logic;

import org.una.presentation.model.Message;
import org.una.presentation.model.User;

import java.io.IOException;

public interface IService {
    public User register(User u) throws Exception;
    public User login(User u) throws Exception;
    public void logout(User u) throws IOException;
    public void post(Message m);
}
