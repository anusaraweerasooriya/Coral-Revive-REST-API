package com.project.rest.user_recommendation_service.service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ModelTrainingScheduler {

    private static final String FLASK_API_URL = "http://flask-server:5000/api/update/train-model";

    private final RestTemplate restTemplate;

    public ModelTrainingScheduler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void scheduleModelTraining() {
        try {
            restTemplate.postForEntity(FLASK_API_URL, null, String.class);
            System.out.println("Model training request sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send model training request: " + e.getMessage());
        }
    }
}
