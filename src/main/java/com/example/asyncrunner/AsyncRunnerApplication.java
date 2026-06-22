package com.example.asyncrunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AsyncRunnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncRunnerApplication.class, args);
	}

}
