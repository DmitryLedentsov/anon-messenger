package com.dimka228.messenger.exceptions;

public class CannotBanSelfException extends WrongPrivilegesException {
  public CannotBanSelfException() {
    super("unable to ban yourself");
  }
}
