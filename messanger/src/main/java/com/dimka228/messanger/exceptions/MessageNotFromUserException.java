package com.dimka228.messanger.exceptions;

public class MessageNotFromUserException extends AppException{
    public MessageNotFromUserException(Integer id, Integer userId){
        super("Message does not belong to the user. msg_id: " + id.toString() + " : user_id: " + userId.toString());
    }
}
