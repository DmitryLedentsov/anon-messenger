package com.dimka228.messenger.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "messenger.websocket")
public class WebSocketProperties {
    
    private  String path="/ws";
    private String url="";

    // standard getters and setters
}