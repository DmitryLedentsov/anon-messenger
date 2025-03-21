package com.dimka228.messenger.services.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.services.interfaces.NotificationService;

import lombok.AllArgsConstructor;

//@Service
@AllArgsConstructor
public class KafkaProducer implements NotificationService{
    private final KafkaTemplate<String, Object> simpleProducer;

    public void sendMessageOperationToChat(Integer chatId, OperationDTO<MessageDTO> operationDTO){
        simpleProducer.send("messages-update", new KafkaMessageDTO<MessageDTO>(chatId,operationDTO));
        simpleProducer.flush();
    } ;
    public void sendChatOperationToUser(Integer userId, OperationDTO<ChatDTO> operationDTO){
        simpleProducer.send("chats-update", new KafkaMessageDTO<ChatDTO>(userId,operationDTO));
        simpleProducer.flush();
    } ;
}