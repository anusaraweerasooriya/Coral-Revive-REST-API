package com.project.rest.user_recommendation_service.repository;

import com.project.rest.user_recommendation_service.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    // Find all comments by a specific user
    List<Comment> findByUserId(String userId);

    // Find all comments for a specific question
    List<Comment> findByQuestionId(String questionId);
}
