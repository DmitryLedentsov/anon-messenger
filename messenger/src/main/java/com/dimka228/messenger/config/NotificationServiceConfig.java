package com.dimka228.messenger.config;

import com.dimka228.messenger.services.kafka.KafkaProducer;
import com.dimka228.messenger.services.SocketMessagingService;
import com.dimka228.messenger.services.interfaces.NotificationService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.client.WebSocketClient;
@Configuration
public class NotificationServiceConfig {

    @Bean(name = "notificationService")
    @ConditionalOnProperty(name = "messenger.multi-instance", havingValue = "true")
    public NotificationService kafkaProducer(KafkaTemplate<String,Object> tmp) {
        return new KafkaProducer(tmp);
    }

    @Bean(name = "notificationService")
    @ConditionalOnProperty(name = "messenger.multi-instance", havingValue = "false", matchIfMissing = true)
    public NotificationService socketMessagingService(SimpMessagingTemplate tmp) {
        return new SocketMessagingService(tmp);
    }

    @Bean
    public SocketMessagingService kafkaSocketMessagingService(SimpMessagingTemplate tmp) {
        return new SocketMessagingService(tmp);
    }
}