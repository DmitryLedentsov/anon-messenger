package com.dimka228.messanger.exceptions;

public class UserExistsException extends AppException{
 
    public UserExistsException(){
        super("user exists");
    }
}
