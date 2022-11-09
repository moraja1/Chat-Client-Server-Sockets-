package org.una.logic;

import org.una.presentation.model.Message;
import org.una.presentation.model.User;

public interface IService {
    public User login(User u) throws Exception;
    public void logout(User u) throws Exception; 
    public void post(Message m);
}
