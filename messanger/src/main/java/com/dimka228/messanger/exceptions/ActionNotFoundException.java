package com.dimka228.messanger.exceptions;

public class ActionNotFoundException extends AppException{
    public ActionNotFoundException(String name){
        super(name);
    }
}
