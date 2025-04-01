package com.dimka228.messenger.config.properties;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "messenger.websocket")
public class WebSocketProperties {

	private String path = "/ws";

	private String url = "";

	// standard getters and setters

}
