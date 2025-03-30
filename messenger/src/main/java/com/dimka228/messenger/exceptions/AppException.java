package com.dimka228.messenger.exceptions;

public class AppException extends RuntimeException {

	public AppException(String chat) {
		super(chat);
	}

	public AppException() {
		super();
	}

}
