package com.websocks.websocks;

import java.util.HashMap;
import java.util.Map;

import com.websocks.websocks.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebsocksApplication implements CommandLineRunner {

	@Bean
	public Map<String, String> getSessionManager() {
		return new HashMap<>();
	}

	@Autowired
	FileService fileService;

	public static void main(String[] args) {

		SpringApplication.run(WebsocksApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fileService.deleteAll();
		fileService.init();
	}
}
