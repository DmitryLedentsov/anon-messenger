package com.dimka228.messenger.services.kafka;

import org.springframework.kafka.core.KafkaTemplate;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.services.interfaces.NotificationService;

import lombok.AllArgsConstructor;

//@Service
@AllArgsConstructor
public class KafkaProducer implements NotificationService {

	private final KafkaTemplate<String, Object> simpleProducer;

	@Override
	public void sendMessageOperationToChat(Integer chatId, OperationDTO<MessageDTO> operationDTO) {
		simpleProducer.send("messages-update", new KafkaMessageDTO<>(chatId, operationDTO));
		simpleProducer.flush();
	};

	@Override
	public void sendChatOperationToUser(Integer userId, OperationDTO<ChatDTO> operationDTO) {
		simpleProducer.send("chats-update", new KafkaMessageDTO<>(userId, operationDTO));
		simpleProducer.flush();
	};

}