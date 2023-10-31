package com.dimka228.messanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MessangerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessangerApplication.class, args);
	}

}
