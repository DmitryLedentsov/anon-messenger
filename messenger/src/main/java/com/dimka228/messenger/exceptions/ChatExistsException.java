package com.dimka228.messenger.exceptions;

public class ChatExistsException extends AppException {
  public ChatExistsException(String chat) {
    super("chat exists" + chat);
  }
}
