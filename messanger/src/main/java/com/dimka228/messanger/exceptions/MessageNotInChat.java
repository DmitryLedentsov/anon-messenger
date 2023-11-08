package com.dimka228.messanger.exceptions;

public class MessageNotInChat extends AppException{
    public MessageNotInChat(Integer msgId, Integer chatId){
        super("msg: "+msgId.toString() + " chat: " + chatId.toString());
    }
}
