package com.dimka228.messanger.exceptions;

public class MessageNotFoundException extends AppException{
    public MessageNotFoundException(Integer id){
        super("message not found "+ Integer.toString(id));
    }
}
