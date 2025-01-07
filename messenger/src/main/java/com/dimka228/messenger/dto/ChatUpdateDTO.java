package com.dimka228.messenger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_DEFAULT)
public class ChatUpdateDTO {
    private Integer id;
    private OperationDTO<MessageDTO> messageData;
    private OperationDTO<ChatDTO> chatData;
}
