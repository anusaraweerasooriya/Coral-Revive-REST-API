package com.project.rest.user_recommendation_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.rest.user_recommendation_service.service.impl.InteractionDataService;
import com.project.rest.user_recommendation_service.service.impl.KnowledgeGraphService; 

@RestController
@RequestMapping("user-recommendation-service/interactions")
public class InteractionController {

    @Autowired
    private InteractionDataService interactionDataService;

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @PostMapping("/generate-ratings")
    public ResponseEntity<String> generateRatingsFile() {
        try {
            // Use environment variables for file paths
            String ratingsFilePath = System.getenv("RATINGS_FILE_PATH");
            String userMapFilePath = System.getenv("USER_MAP_FILE_PATH");
            String itemMapFilePath = System.getenv("ITEM_MAP_FILE_PATH");

            interactionDataService.generateRatingsFile(ratingsFilePath, userMapFilePath, itemMapFilePath);

            return ResponseEntity.ok("Ratings file generated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to generate ratings file: " + e.getMessage());
        }
    }

    @PostMapping("/generate-kg")
    public ResponseEntity<String> generateKgFile() {
        try {
            // Use environment variables for file paths
            String kgFilePath = System.getenv("KG_FILE_PATH");
            String userMapFilePath = System.getenv("USER_MAP_FILE_PATH");
            String postMapFilePath = System.getenv("POST_MAP_FILE_PATH");
            
            // Generate the knowledge graph file and mappings
            knowledgeGraphService.generateKgFile(kgFilePath, userMapFilePath, postMapFilePath);
            
            return ResponseEntity.ok("Knowledge graph file generated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to generate knowledge graph file: " + e.getMessage());
        }
    }
}
