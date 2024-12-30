package com.dimka228.messenger.config;

import com.dimka228.messenger.dto.ChatUpdateDTO;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, ChatUpdateDTO> userProducerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
    configProps.put(ProducerConfig.LINGER_MS_CONFIG, "50");
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public NewTopic topic1() {
    return TopicBuilder.name("chat-update").partitions(1).replicas(1).build();
  }

  @Bean
  public NewTopic topic2() {
    return TopicBuilder.name("chats-update").partitions(1).replicas(1).build();
  }

  @Bean
  public KafkaTemplate<String, ChatUpdateDTO> userKafkaTemplate() {
    return new KafkaTemplate<>(userProducerFactory());
  }
}
