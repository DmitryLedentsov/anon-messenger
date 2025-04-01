package com.dimka228.messenger.exceptions;

public class WrongPrivilegesException extends AppException {

	public WrongPrivilegesException() {
		super("Not enough privileges");
	}

	public WrongPrivilegesException(String s) {
		super(s);
	}

}
