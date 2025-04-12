package com.dimka228.messenger.exceptions;

public class UserAlreadyInChatException extends AppException {
    
	public UserAlreadyInChatException() {
		super("user already in chat");
	}
}
