package com.project.rest.user_recommendation_service.service.api;

import com.project.rest.user_recommendation_service.model.Feed;

public interface FeedService {
    Feed getUserFeed(String userId);
}
