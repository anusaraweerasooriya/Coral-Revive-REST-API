package com.project.rest.user_recommendation_service.repository;

import com.project.rest.user_recommendation_service.model.Feed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedRepository extends MongoRepository<Feed, String> {
    // Find a feed by user ID
    Optional<Feed> findByUserId(String userId);
}
