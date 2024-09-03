package com.project.rest.user_recommendation_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceClient.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final String AUTH_SERVICE_URL = "http://auth-service/auth-service/validate-token";

    public boolean validateToken(String token) {
        // Ensure the token is in the correct format
        if (token == null || !token.startsWith("Bearer ")) {
            logger.error("Invalid token format: {}", token);
            return false;
        }

        // Log the token being validated
        logger.info("Validating token: {}", token);

        // Create headers and include the token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Send the request to validate the token
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    AUTH_SERVICE_URL,
                    HttpMethod.GET,
                    entity,
                    Boolean.class
            );

            // Log the response from the auth service
            logger.info("Response from auth service: {}", response.getBody());

            return response.getBody() != null && response.getBody();
        } catch (Exception e) {
            logger.error("Error during token validation: {}", e.getMessage());
            return false;
        }
    }
}

