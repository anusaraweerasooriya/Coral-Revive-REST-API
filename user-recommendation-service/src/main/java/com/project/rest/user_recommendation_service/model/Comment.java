package com.project.rest.user_recommendation_service.model;

import java.util.Date;

public class Comment {
    private String id;
    private String questionId;  // Field to link the comment to a question
    private String userId;  // ID of the user who made the comment
    private String content;  // The content of the comment
    private Date createdAt;  // Date when the comment was created
    private String verified;  // Can be "semantic", "false", or "true"

    // Constructors
    public Comment() {}

    public Comment(String id, String questionId, String userId, String content, Date createdAt) {
        this.id = id;
        this.questionId = questionId;  // Initialize the questionId field
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

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
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

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
