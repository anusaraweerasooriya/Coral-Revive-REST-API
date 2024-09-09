package com.project.rest.user_recommendation_service.dto;

public class TokenValidationResponseDTO {
    private boolean isValid;
    private String userId;

    // Default constructor
    public TokenValidationResponseDTO() {}

    // Parameterized constructor
    public TokenValidationResponseDTO(boolean isValid, String userId) {
        this.isValid = isValid;
        this.userId = userId;
    }

    // Getters and Setters
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TokenValidationResponseDTO{" +
                "isValid=" + isValid +
                ", userId='" + userId + '\'' +
                '}';
    }
}
