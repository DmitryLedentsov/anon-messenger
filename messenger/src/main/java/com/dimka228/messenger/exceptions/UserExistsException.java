package com.dimka228.messenger.exceptions;

public class UserExistsException extends AppException {

  public UserExistsException() {
    super("user exists");
  }
}
