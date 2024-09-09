package com.project.rest.user_recommendation_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.rest.user_recommendation_service.service.impl.InteractionDataService;
import com.project.rest.user_recommendation_service.service.impl.KnowledgeGraphService; 

@RestController
@RequestMapping("/interactions")
public class InteractionController {

    @Autowired
    private InteractionDataService interactionDataService;

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @PostMapping("/generate-ratings")
    public ResponseEntity<String> generateRatingsFile() {
    try {
        String ratingsFilePath = "/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/ratings_final.txt";
        String userMapFilePath = "/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/user_mapping.json";
        String itemMapFilePath = "/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/item_mapping.json";

        interactionDataService.generateRatingsFile(ratingsFilePath, userMapFilePath, itemMapFilePath);

        return ResponseEntity.ok("Ratings file generated successfully.");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Failed to generate ratings file: " + e.getMessage());
    }
    }


    @PostMapping("/generate-kg")
    public ResponseEntity<String> generateKgFile() {
        try {
            String kgFilePath = "/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/kg_final.txt";
            String userMapFilePath = "/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/user_mapping.json";
            String postMapFilePath = "/Users/seminipeiris/Desktop/Coral-Revive-REST-API/user-recommendation-service/post_mapping.json";
            
            // Generate the knowledge graph file and mappings
            knowledgeGraphService.generateKgFile(kgFilePath, userMapFilePath, postMapFilePath);
            
            return ResponseEntity.ok("Knowledge graph file generated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to generate knowledge graph file: " + e.getMessage());
        }
    }

}
