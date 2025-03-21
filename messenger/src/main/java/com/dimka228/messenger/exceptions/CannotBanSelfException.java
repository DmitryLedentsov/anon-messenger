package com.dimka228.messenger.exceptions;

public class CannotBanSelfException extends WrongPrivilegesException {
    public CannotBanSelfException() {
        super("Unable to ban yourself");
    }
}
