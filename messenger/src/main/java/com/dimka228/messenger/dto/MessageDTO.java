package com.dimka228.messenger.dto;

import com.dimka228.messenger.models.MessageInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_DEFAULT)
public class MessageDTO implements MessageInfo {

	private Integer id;

	private String message;

	private Integer senderId;

	private String sender;

	private String sendTime;

}
