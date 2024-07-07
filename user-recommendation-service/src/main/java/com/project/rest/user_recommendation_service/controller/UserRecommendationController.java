package com.project.rest.user_recommendation_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRecommendationController {

    @GetMapping("/user-recommendation-service")
    public ResponseEntity<?> initService() {
        return new ResponseEntity<>("User Recommendation Service Initialized", HttpStatus.OK);

    }
}
