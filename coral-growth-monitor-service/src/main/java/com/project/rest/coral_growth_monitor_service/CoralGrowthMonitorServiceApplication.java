package com.project.rest.coral_growth_monitor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
public class CoralGrowthMonitorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoralGrowthMonitorServiceApplication.class, args);
	}

}
