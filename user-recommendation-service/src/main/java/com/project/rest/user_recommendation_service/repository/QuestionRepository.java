package com.project.rest.user_recommendation_service.repository;

import com.project.rest.user_recommendation_service.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    // Find all questions by a specific user
    List<Question> findByUserId(String userId);

    // Find all questions liked by a specific user
    List<Question> findByLikedByContaining(String userId);

    // Find all questions shared by a specific user
    List<Question> findBySharedByContaining(String userId);
}
