package com.dimka228.messenger.exceptions;

public class WrongTokenException extends AppException {
    public WrongTokenException() {
        super("Wrong token");
    }

    public WrongTokenException(String msg) {
        super(msg);
    }
}
