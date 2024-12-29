package com.dimka228.messenger.dto;

import com.dimka228.messenger.models.MessageInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_DEFAULT)
public class ChatDTO {
  Integer id;
  String name;
  String role;
  MessageInfo lastMessage;
  List<String> users;
}
