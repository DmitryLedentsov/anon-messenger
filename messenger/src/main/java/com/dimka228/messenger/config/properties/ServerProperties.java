package com.dimka228.messenger.config.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
@Data
@Configuration
@ConfigurationProperties(prefix = "messenger")
public class ServerProperties {
    private  String publicUrl="";
}
