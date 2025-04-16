package com.dimka228.messenger.services;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.services.interfaces.NotificationService;

import lombok.AllArgsConstructor;

//@Service
@AllArgsConstructor
public class SocketMessagingService implements NotificationService {

	private final SimpMessagingTemplate msgTemplate;

	@Override
	public void sendMessageOperationToChat(Integer chatId, OperationDTO<MessageDTO> operationDTO) {
		msgTemplate.convertAndSend("/topic/chat/" + chatId + "/messages", operationDTO);
	}

	@Override
	public void sendChatOperationToUser(Integer userId, OperationDTO<ChatDTO> operationDTO) {
		msgTemplate.convertAndSend("/topic/user/" + userId + "/chats", operationDTO);
	}

	public void notifyMembersThatChatWasCreated(List<User> users, ChatDTO chatDTO) {
	}

}
