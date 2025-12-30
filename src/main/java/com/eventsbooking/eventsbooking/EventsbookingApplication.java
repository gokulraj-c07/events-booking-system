package com.eventsbooking.eventsbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventsbookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventsbookingApplication.class, args);
	}

}
