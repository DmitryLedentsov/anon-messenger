package com.dimka228.messanger.dto;

import com.dimka228.messanger.models.MessageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatDTO {
    Integer id;
    String name;
    String role;
    MessageInfo lastMessage;
    List<String> users;
}
