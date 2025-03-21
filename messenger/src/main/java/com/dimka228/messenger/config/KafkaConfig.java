package com.dimka228.messenger.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
@ConditionalOnProperty(name = "messenger.multi-instance")
public class KafkaConfig {
  /*  @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("chats-update").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("messages-update").partitions(1).replicas(1).build();
    }*/
}
