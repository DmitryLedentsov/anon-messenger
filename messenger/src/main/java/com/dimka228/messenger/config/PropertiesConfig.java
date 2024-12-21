package com.dimka228.messenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@PropertySource("classpath:application-net.properties")
@PropertySource("classpath:application-docker.properties")
public class PropertiesConfig { }