package com.dimka228.messanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.dimka228.messanger.crypt.GostPasswordEncoder;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MessangerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessangerApplication.class, args);
	}
	@Bean
	public GostPasswordEncoder passwordEncoder() {
		return new GostPasswordEncoder();
	}
}
