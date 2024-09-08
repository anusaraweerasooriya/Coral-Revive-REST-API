package com.project.rest.user_recommendation_service.dto;

public class CommentClassificationResponseDTO {
    private String classification;

    // Constructors
    public CommentClassificationResponseDTO() {}

    public CommentClassificationResponseDTO(String classification) {
        this.classification = classification;
    }

    // Getters and Setters
    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
