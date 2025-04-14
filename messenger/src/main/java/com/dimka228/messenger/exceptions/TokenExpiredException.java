package com.dimka228.messenger.exceptions;

public class TokenExpiredException extends WrongTokenException {

	public TokenExpiredException() {
		super("token expired");
	}

}
