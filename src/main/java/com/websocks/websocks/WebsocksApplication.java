package com.websocks.websocks;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.websocks.websocks.services.FileService;

@SpringBootApplication
public class WebsocksApplication implements CommandLineRunner {

	@Autowired
	FileService fileService;

	@Bean
	Map<String, String> getSessionManager() {
		return new HashMap<>();
	}

	@Override
	public void run(String... args) throws Exception {
		fileService.deleteAll();
		fileService.init();
	}

	public static void main(String[] args) {
		SpringApplication.run(WebsocksApplication.class, args);
	}
}
