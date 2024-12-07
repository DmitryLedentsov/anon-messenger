package com.dimka228.messenger.dto;

import com.dimka228.messenger.models.MessageInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatDTO {
  Integer id;
  String name;
  String role;
  MessageInfo lastMessage;
  List<String> users;
}
