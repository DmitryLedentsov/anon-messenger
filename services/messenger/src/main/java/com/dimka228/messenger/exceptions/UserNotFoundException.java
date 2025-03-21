package com.dimka228.messenger.exceptions;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String login) {
        super("User not found: " + login);
    }

    public UserNotFoundException() {
        super("User not found ");
    }
}
