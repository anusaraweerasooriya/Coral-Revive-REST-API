package com.project.rest.user_recommendation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
public class UserRecommendationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserRecommendationServiceApplication.class, args);
	}

}
