package com.websocks.websocks;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebsocksApplication {

	@Bean
	public Map<String, String> getSessionManager() {
		return new HashMap<>();
	}

	public static void main(String[] args) {
		SpringApplication.run(WebsocksApplication.class, args);
	}

}
