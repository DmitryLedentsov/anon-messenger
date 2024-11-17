package com.dimka228.messanger.exceptions;

public class UserNotFoundException extends AppException{
    public UserNotFoundException(String login){
        super("user not found: "+login);
    }
    public UserNotFoundException(){
        super("user not found ");
    }
}