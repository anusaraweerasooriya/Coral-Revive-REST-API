package com.project.rest.user_recommendation_service.model;

import java.util.List;
import java.util.Map;

public class Feed {
    private String userId;
    private List<Map<String, Object>> posts; // Changed from List<Question> to List<Map<String, Object>>

    // Constructors
    public Feed() {}

    public Feed(String userId, List<Map<String, Object>> posts) {
        this.userId = userId;
        this.posts = posts;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Map<String, Object>> getPosts() {
        return posts;
    }

    public void setPosts(List<Map<String, Object>> posts) {
        this.posts = posts;
    }
}
