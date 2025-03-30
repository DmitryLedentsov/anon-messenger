package com.dimka228.messenger.exceptions;

public class UserNotInChatException extends AppException {

	public UserNotInChatException(Integer userId, Integer chatId) {
		super("User not in chat. user: " + Integer.toString(userId) + " chat: " + Integer.toString(chatId));
	}

}
