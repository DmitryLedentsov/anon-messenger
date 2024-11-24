package com.dimka228.messenger.exceptions;

public class ChatNotFoundException extends AppException {
  public ChatNotFoundException(Integer chat) {
    super("chat not found " + Integer.toString(chat));
  }
}
