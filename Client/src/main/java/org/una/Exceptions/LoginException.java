package org.una.Exceptions;

public class LoginException extends Exception{

    public LoginException(){
        super("Error inciando sesión.");
    }

    public LoginException(String message) {
        super(message);
    }
}
