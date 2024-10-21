package com.project.rest.user_recommendation_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KGCNRecommendationClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String FLASK_SERVICE_URL = "http://flask-server:5000/api/recommend";

    public List<Integer> getRecommendedItems(Integer userIndex, List<Integer> itemIndices) {
        // Create a request body with user index and item indices
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_index", userIndex);
        requestBody.put("item_indices", itemIndices);

        // Send POST request to Flask service and get the response
        List<Integer> recommendedItems = restTemplate.postForObject(
            FLASK_SERVICE_URL, requestBody, List.class);

        // Return the list of recommended items or an empty list if the response is null
        return recommendedItems != null ? recommendedItems : List.of();
    }
}
