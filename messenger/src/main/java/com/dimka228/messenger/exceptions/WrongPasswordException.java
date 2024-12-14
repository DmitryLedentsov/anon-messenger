package com.dimka228.messenger.exceptions;

public class WrongPasswordException extends AppException {
  public WrongPasswordException() {
    super("wrong password");
  }

  public WrongPasswordException(String s) {
    super(s);
  }
}
