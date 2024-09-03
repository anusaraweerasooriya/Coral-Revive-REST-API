package com.project.rest.user_recommendation_service.service.api;

import com.project.rest.user_recommendation_service.model.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Comment comment);
    Comment getCommentById(String id);
    List<Comment> getCommentsByQuestionId(String questionId);
    List<Comment> getCommentsByUserId(String userId);
}

