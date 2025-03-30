package com.dimka228.messenger.config;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@ConditionalOnProperty({ "messenger.multi-instance", "messenger.kafka.auto-config-group" })

public class KafkaConfig {

	@Value("${server.port}")
	private String groupId;

	private KafkaProperties properties;

	@Autowired
	public KafkaConfig(KafkaProperties p) {
		properties = p;
	}

	@Bean
	public ConsumerFactory<String, Object> consumerFactory() {

		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = properties.buildConsumerProperties();

		props.put(ConsumerConfig.GROUP_ID_CONFIG, "updates_" + groupId);
		return props;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

}