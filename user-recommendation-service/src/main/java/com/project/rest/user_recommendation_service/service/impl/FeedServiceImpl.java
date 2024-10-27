package com.project.rest.user_recommendation_service.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rest.user_recommendation_service.dto.UserDTO;
import com.project.rest.user_recommendation_service.model.Feed;
import com.project.rest.user_recommendation_service.model.Question;
import com.project.rest.user_recommendation_service.repository.QuestionRepository;
import com.project.rest.user_recommendation_service.service.UserAuthClient;
import com.project.rest.user_recommendation_service.service.api.FeedService;

@Service
public class FeedServiceImpl implements FeedService {

    private static final Logger logger = LoggerFactory.getLogger(FeedServiceImpl.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserAuthClient userAuthClient;

    @Override
    public Feed getUserFeed(String userId) {
        logger.info("Generating feed for user ID: {}", userId);
        List<Map<String, Object>> feedPosts = new ArrayList<>();

        // Fetch the user's own posts
        logger.info("Fetching user's own posts for user ID: {}", userId);
        List<Question> userOwnPosts = questionRepository.findByUserId(userId);
        logger.info("Found {} posts for the user ID: {}", userOwnPosts.size(), userId);

        for (Question post : userOwnPosts) {
            logger.info("Fetching user details for post ID: {}", post.getId());
            UserDTO userDTO = userAuthClient.getUserDetails(post.getUserId());
            logger.info("User details fetched for user ID: {}", post.getUserId());

            Map<String, Object> postWithUser = new HashMap<>();
            postWithUser.put("question", post);
            postWithUser.put("user", userDTO);
            feedPosts.add(postWithUser);
        }

        // Fetch posts from users the current user is following
        List<String> followedUserIds = getFollowedUserIds(userId);
        logger.info("User {} is following {} users", userId, followedUserIds.size());

        for (String followedUserId : followedUserIds) {
            logger.info("Fetching posts for followed user ID: {}", followedUserId);
            // Fetch original posts by the followed user
            List<Question> originalPosts = questionRepository.findByUserId(followedUserId);
            logger.info("Found {} original posts for user ID: {}", originalPosts.size(), followedUserId);

            for (Question post : originalPosts) {
                logger.info("Fetching user details for post ID: {}", post.getId());
                UserDTO userDTO = userAuthClient.getUserDetails(post.getUserId());
                logger.info("User details fetched for user ID: {}", post.getUserId());

                Map<String, Object> postWithUser = new HashMap<>();
                postWithUser.put("question", post);
                postWithUser.put("user", userDTO);
                feedPosts.add(postWithUser);
            }

            // Fetch shared posts by the followed user
            List<Question> sharedPosts = questionRepository.findBySharedByContaining(followedUserId);
            logger.info("Found {} shared posts for user ID: {}", sharedPosts.size(), followedUserId);

            for (Question post : sharedPosts) {
                logger.info("Fetching user details for shared post ID: {}", post.getId());
                UserDTO userDTO = userAuthClient.getUserDetails(post.getUserId());
                logger.info("User details fetched for user ID: {}", post.getUserId());

                Map<String, Object> postWithUser = new HashMap<>();
                postWithUser.put("question", post);
                postWithUser.put("user", userDTO);
                feedPosts.add(postWithUser);
            }
        }

        // Fetch and mark recommended posts
        List<Question> recommendedPosts = getRecommendedPostsForUser(userId);
        logger.info("Found {} recommended posts for user ID: {}", recommendedPosts.size(), userId);

        for (Question recommendedPost : recommendedPosts) {
            recommendedPost.setRecommended(true);
            logger.info("Fetching user details for recommended post ID: {}", recommendedPost.getId());
            UserDTO userDTO = userAuthClient.getUserDetails(recommendedPost.getUserId());
            logger.info("User details fetched for user ID: {}", recommendedPost.getUserId());

            Map<String, Object> postWithUser = new HashMap<>();
            postWithUser.put("question", recommendedPost);
            postWithUser.put("user", userDTO);
            feedPosts.add(postWithUser);
        }

        logger.info("Feed generation complete for user ID: {}", userId);
        return new Feed(userId, feedPosts);
    }

    private List<Integer> fetchRecommendationsFromFlask(Integer userIndex) {  // Remove itemIndices parameter
        RestTemplate restTemplate = new RestTemplate();
        String flaskUrl = "http://flask-server:5000/api/recommend";
    
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_index", userIndex);  // Only include user_index
    
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(flaskUrl, requestBody, (Class<Map<String, Object>>)(Class<?>)Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("recommended_items")) {
                    return (List<Integer>) responseBody.get("recommended_items");
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching recommendations from Flask service", e);
        }
    
        return new ArrayList<>();
    }
    
    // Fetch the followed user IDs from the user-auth service
    private List<String> getFollowedUserIds(String userId) {
        logger.info("Fetching followed user IDs for user ID: {}", userId);
        UserDTO user = userAuthClient.getUserDetails(userId);
        if (user != null) {
            logger.info("User ID {} is following {} users", userId, user.getFollowing().size());
            return user.getFollowing();
        } else {
            logger.warn("No user details found for user ID: {}", userId);
            return new ArrayList<>();
        }
    }



    private List<Question> getRecommendedPostsForUser(String userId) {
        logger.info("Fetching recommended posts for user ID: {}", userId);
        
        // Load user and item mappings
        String userMapFilePath = System.getenv("USER_MAP_FILE_PATH");
        String itemMapFilePath = System.getenv("ITEM_MAP_FILE_PATH");
    
        Map<String, Integer> userMap = loadMapping(userMapFilePath);
        Map<String, Integer> itemMap = loadMapping(itemMapFilePath);
    
        Integer userIndex = userMap.get(userId);
        if (userIndex == null) {
            logger.warn("No index found for user ID: {}", userId);
            return new ArrayList<>();
        }
    
        List<Integer> recommendedItemIndices = fetchRecommendationsFromFlask(userIndex);
    
        if (recommendedItemIndices.isEmpty()) {
            logger.warn("No recommendations returned for user ID: {}", userId);
            return new ArrayList<>();
        }
    
        // Convert the recommended item indices back to their corresponding String IDs
        List<String> recommendedItemIds = recommendedItemIndices.stream()
                .map(index -> getKeyByValue(itemMap, index))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    
        // Fetch the questions (posts) corresponding to the recommended item IDs
        return questionRepository.findAllById(recommendedItemIds);
    }
    
    // Helper method to get a key (String ID) by its value (Integer index) in a map
    private <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private Map<String, Integer> loadMapping(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(filePath), Map.class);
        } catch (Exception e) {
            logger.error("Error loading mapping from file: {}", filePath, e);
            return new HashMap<>();
        }
    }
}
