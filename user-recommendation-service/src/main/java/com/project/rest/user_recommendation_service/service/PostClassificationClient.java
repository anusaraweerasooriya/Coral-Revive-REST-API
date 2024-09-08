package com.project.rest.user_recommendation_service.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rest.user_recommendation_service.dto.PostClassificationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PostClassificationClient {

    @Autowired
    private RestTemplate loadBalancedRestTemplate;

    private static final String FLASK_SERVICE_URL = "http://127.0.0.1:5000/api/classifyPost";

    public List<String> classifyPost(String postContent) {
        PostClassificationRequestDTO requestDTO = new PostClassificationRequestDTO(postContent);

        try {
            // Get the raw response as a String
            String rawResponse = loadBalancedRestTemplate.postForObject(FLASK_SERVICE_URL, requestDTO, String.class);
            System.out.println("Raw Flask API Response: " + rawResponse);

            // Parse the raw response to extract the "top_two_labels"
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(rawResponse);
            JsonNode labelsNode = jsonNode.get("top_two_labels");

            // Convert the JSON array to a List<String>
            List<String> topTwoLabels = objectMapper.convertValue(labelsNode, List.class);
            System.out.println("Extracted top_two_labels: " + topTwoLabels);

            return topTwoLabels != null ? topTwoLabels : List.of();

        } catch (Exception e) {
            System.err.println("Error while extracting top_two_labels: " + e.getMessage());
            return List.of(); // Return an empty list in case of failure
        }
    }
}
