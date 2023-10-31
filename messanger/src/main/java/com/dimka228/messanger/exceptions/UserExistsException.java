package com.dimka228.messanger.exceptions;

public class UserExistsException extends RuntimeException{
    public UserExistsException(String login){
        super(login);
    }
}
