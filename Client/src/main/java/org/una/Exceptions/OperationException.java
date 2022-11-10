package org.una.Exceptions;

public class OperationException extends Exception{
    public OperationException(){
        super("Error en la operación");
    }
    public OperationException(String message){
        super(message);
    }
}
