package com.dimka228.messenger.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatDtoRequest {
  String name;
  List<String> users;
}
