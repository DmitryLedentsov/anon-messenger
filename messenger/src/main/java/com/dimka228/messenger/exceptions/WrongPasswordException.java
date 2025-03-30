package com.dimka228.messenger.exceptions;

public class WrongPasswordException extends AppException {

	public WrongPasswordException() {
		super("Wrong password");
	}

	public WrongPasswordException(String s) {
		super(s);
	}

}
