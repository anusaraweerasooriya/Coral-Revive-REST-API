package com.project.rest.user_recommendation_service.dto;

public class TokenValidationRequestDTO {
    private String token;
    private String userId;

    // Default constructor
    public TokenValidationRequestDTO() {}

    // Parameterized constructor
    public TokenValidationRequestDTO(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TokenValidationRequestDTO{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
