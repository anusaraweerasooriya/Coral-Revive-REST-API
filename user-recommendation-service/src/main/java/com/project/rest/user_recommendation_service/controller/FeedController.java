package com.project.rest.user_recommendation_service.controller;

import com.project.rest.user_recommendation_service.model.Feed;
import com.project.rest.user_recommendation_service.service.AuthServiceClient;
import com.project.rest.user_recommendation_service.service.api.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @Autowired
    private AuthServiceClient authServiceClient;

    // Endpoint to get the user's feed
    @GetMapping("/{userId}")
    public ResponseEntity<Feed> getUserFeed(@RequestHeader("Authorization") String token,
                                            @PathVariable String userId) {
        // Validate the token
        boolean isTokenValid = authServiceClient.validateToken(token);
        if (!isTokenValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Feed userFeed = feedService.getUserFeed(userId);
        return ResponseEntity.ok(userFeed);
    }
}
