package com.dimka228.messanger.dto;

import com.dimka228.messanger.models.MessageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDTO implements MessageInfo {
    private Integer id;
    private String message;
    private Integer senderId;
    private String sender;
}
