package com.dimka228.messenger.exceptions;

public class ActionNotFoundException extends AppException {
  public ActionNotFoundException(String name) {
    super(name);
  }
}
