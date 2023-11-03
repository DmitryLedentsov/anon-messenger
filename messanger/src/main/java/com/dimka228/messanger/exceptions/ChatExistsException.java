package com.dimka228.messanger.exceptions;

public class ChatExistsException extends RuntimeException{
    public ChatExistsException(String chat){
        super(chat);
    }
}
