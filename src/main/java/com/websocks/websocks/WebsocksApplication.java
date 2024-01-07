package com.websocks.websocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.websocks.websocks.services.FileService;

@EnableScheduling
@SpringBootApplication
public class WebsocksApplication implements CommandLineRunner {

	@Autowired
	FileService fileService;

	@Bean
	Map<String, String> getSessionManager() {
		return new ConcurrentHashMap<>();
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
