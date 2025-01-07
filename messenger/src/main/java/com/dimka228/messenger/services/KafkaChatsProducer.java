package com.dimka228.messenger.services;

import com.dimka228.messenger.dto.ChatUpdateDTO;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaChatsProducer {

    private final KafkaTemplate<String, ChatUpdateDTO> kafkaProducer;

    public KafkaChatsProducer(KafkaTemplate<String, ChatUpdateDTO> simpleProducer) {
        this.kafkaProducer = simpleProducer;
    }

    public void sendChatsUpdate(ChatUpdateDTO message) {
        kafkaProducer.send("chats-update", message);
        kafkaProducer.flush();
    }

    public void sendChatUpdate(ChatUpdateDTO message) {
        kafkaProducer.send("chat-update", message);
        kafkaProducer.flush();
    }
}
