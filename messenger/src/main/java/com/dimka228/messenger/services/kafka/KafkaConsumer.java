package com.dimka228.messenger.services.kafka;


import com.dimka228.messenger.services.SocketMessagingService;

import lombok.AllArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
@Component
@ConditionalOnProperty(name = "messenger.multi-instance")
@AllArgsConstructor
public class KafkaConsumer {

    private final SocketMessagingService socketMessagingService;

    @KafkaListener(topics = "chats-update")
    public void updateChats(KafkaMessageDTO<ChatDTO> message) {
        socketMessagingService.sendChatOperationToUser(message.getId(), message.getChange());
    }

    @KafkaListener(topics = "messages-update")
    public void updateChat(KafkaMessageDTO<MessageDTO> message) {
        socketMessagingService.sendMessageOperationToChat(message.getId(), message.getChange());
    }
}