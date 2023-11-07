package com.dimka228.messanger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatDTO {
    Integer id;
    String name;
    MessageDTO lastMessage;
    List<String> users;
}
