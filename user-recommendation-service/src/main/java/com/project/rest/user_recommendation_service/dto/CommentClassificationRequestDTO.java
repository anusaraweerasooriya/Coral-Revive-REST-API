package com.project.rest.user_recommendation_service.dto;

public class CommentClassificationRequestDTO {
    private String post;
    private String comment;

    public CommentClassificationRequestDTO() {}

    public CommentClassificationRequestDTO(String post, String comment) {
        this.post = post;
        this.comment = comment;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

