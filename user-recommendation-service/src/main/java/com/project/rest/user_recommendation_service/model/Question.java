package com.project.rest.user_recommendation_service.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Question {
    private String id;
    private String userId; 
    private String content; 
    private Date createdAt;
    private Set<String> likedBy = new HashSet<>(); 
    private Set<String> dislikedBy = new HashSet<>(); 
    private Set<String> sharedBy = new HashSet<>(); 
    private Set<String> comments = new HashSet<>(); 

    // New fields
    private String originalUserId; // ID of the original poster (if shared)
    private boolean isRecommended;
    private Set<String> category = new HashSet<>();

    public Question() {}

    public Question(String id, String userId, String content, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<String> likedBy) {
        this.likedBy = likedBy;
    }

    public Set<String> getDislikedBy() {
        return dislikedBy;
    }

    public void setDislikedBy(Set<String> dislikedBy) {
        this.dislikedBy = dislikedBy;
    }

    public Set<String> getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(Set<String> sharedBy) {
        this.sharedBy = sharedBy;
    }

    public Set<String> getComments() {
        return comments;
    }

    public void setComments(Set<String> comments) {
        this.comments = comments;
    }

    // New Getters and Setters for added fields
    public String getOriginalUserId() {
        return originalUserId;
    }

    public void setOriginalUserId(String originalUserId) {
        this.originalUserId = originalUserId;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
    }

    public Set<String> getCategory() {
        return category;
    }

    public void setCategory(Set<String> category) {
        this.category = category;
    }
}
