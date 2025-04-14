package com.dimka228.messenger;

import java.util.LinkedList;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.dimka228.messenger.entities.Message;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ErrorMvcAutoConfiguration.class })
@EnableScheduling
public class MessengerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessengerApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public LinkedList<Message> linkedList() {
		return new LinkedList<Message>();
	}

	@Bean
	public ReentrantLock reentrantLock() {
		return new ReentrantLock();
	}

	@Bean
	public Timer messageBatchTimer() {
		return new Timer("messageBatchTimer");
	}

	@Bean
	public AtomicBoolean atomicBoolean() {
		return new AtomicBoolean(false);
	}

}
