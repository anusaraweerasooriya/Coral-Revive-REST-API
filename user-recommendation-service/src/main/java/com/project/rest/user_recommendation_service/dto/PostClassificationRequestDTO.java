package com.project.rest.user_recommendation_service.dto;

public class PostClassificationRequestDTO {
    private String post;  // Changed field to 'post' to match the Flask API request format


    public PostClassificationRequestDTO(String post) {
        this.post = post;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "PostClassificationRequestDTO{" +
                "post='" + post + '\'' +
                '}';
    }
}
