package com.dimka228.messenger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class ExampleBean {
  /*/  @Value("${spring.kafka.group-id}")
    private String customProperty;

    public String getCustomProperty() {
        return customProperty+Math.round(Math.random()*1000);
    }*/
}