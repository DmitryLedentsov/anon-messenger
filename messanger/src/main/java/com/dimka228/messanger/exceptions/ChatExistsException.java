package com.dimka228.messanger.exceptions;

public class ChatExistsException extends AppException{
    public ChatExistsException(String chat){
        super("chat exists" + chat);
    }
}
