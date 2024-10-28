package com.yfckevin.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableScheduling
public class ChatbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatbotApplication.class, args);
	}


	@Bean
	public DateTimeFormatter dateTimeFormatter() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	}
}
