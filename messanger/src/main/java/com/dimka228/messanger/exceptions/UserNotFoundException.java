package com.dimka228.messanger.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String login){
        super(login);
    }
}