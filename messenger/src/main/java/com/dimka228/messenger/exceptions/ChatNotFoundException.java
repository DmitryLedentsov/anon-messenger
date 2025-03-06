package com.dimka228.messenger.exceptions;

public class ChatNotFoundException extends AppException {
    public ChatNotFoundException(Integer chat) {
        super("Chat not found " + Integer.toString(chat));
    }
    public ChatNotFoundException() {
        super("Chat not found");
    }
}
