package com.dimka228.messenger.dto;

import com.dimka228.messenger.models.MessageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDTO implements MessageInfo {
  private Integer id;
  private String message;
  private Integer senderId;
  private String sender;
  private String sendTime;
}
