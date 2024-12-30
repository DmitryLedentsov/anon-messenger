package com.dimka228.messenger.services;

import com.dimka228.messenger.dto.ChatUpdateDTO;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SimpleProducer {

    private final KafkaTemplate<String, ChatUpdateDTO> simpleProducer;

    public SimpleProducer(KafkaTemplate<String, ChatUpdateDTO> simpleProducer) {
        this.simpleProducer = simpleProducer;
    }

    public void sendChatsUpdate(ChatUpdateDTO message) {
        simpleProducer.send("chats-update", message);
        simpleProducer.flush();
    }

    public void sendChatUpdate(ChatUpdateDTO message) {
        simpleProducer.send("chat-update", message);
        simpleProducer.flush();
    }
}
