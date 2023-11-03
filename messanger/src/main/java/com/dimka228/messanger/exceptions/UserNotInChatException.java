package com.dimka228.messanger.exceptions;

public class UserNotInChatException extends RuntimeException{
    public UserNotInChatException(Integer userId, Integer chatId){
        super("user: "  + Integer.toString(userId) + " chat: " + Integer.toString(chatId));
    }
}
