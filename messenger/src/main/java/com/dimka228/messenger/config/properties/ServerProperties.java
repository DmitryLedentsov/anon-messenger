package com.dimka228.messenger.config.properties;

import lombok.Data;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "messenger")
public class ServerProperties {

	private String publicUrl = "";

	private List<String> paths;

}
