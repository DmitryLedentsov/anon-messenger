package com.dimka228.messenger.config;

import com.dimka228.messenger.dto.ChatUpdateDTO;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumer {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.group-id}")
  private String groupId;

  public ConsumerFactory<String, ChatUpdateDTO> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");
    props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "250");
    final JsonDeserializer<ChatUpdateDTO> jsonDeserializer = new JsonDeserializer<>();
    jsonDeserializer.addTrustedPackages("*");
    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ChatUpdateDTO>
      kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, ChatUpdateDTO> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
}
