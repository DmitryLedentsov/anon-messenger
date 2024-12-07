package com.dimka228.messenger.exceptions;

public class WrongPasswordException extends AppException {
  public WrongPasswordException() {
    super("wrong passwd");
  }

  public WrongPasswordException(String s) {
    super(s);
  }
}
