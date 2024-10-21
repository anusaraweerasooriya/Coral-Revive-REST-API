package com.project.rest.user_recommendation_service.service.impl;

import com.project.rest.user_recommendation_service.dto.UserDTO;
import com.project.rest.user_recommendation_service.model.Question;
import com.project.rest.user_recommendation_service.repository.QuestionRepository;
import com.project.rest.user_recommendation_service.service.UserAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KnowledgeGraphService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeGraphService.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserAuthClient userAuthClient;

    // Define the mapping of relations to indices
    private static final Map<String, Integer> relationMapping = new HashMap<>();
    private static Map<String, Integer> userMapping = new ConcurrentHashMap<>();
    private static Map<String, Integer> postMapping = new ConcurrentHashMap<>();
    private static int userIdCounter = 1;
    private static int postIdCounter = 1;

    static {
        relationMapping.put("likes", 0);
        relationMapping.put("dislikes", 1);
        relationMapping.put("shares", 2);
        relationMapping.put("original_posted", 3);
        relationMapping.put("follows", 4);
        relationMapping.put("commented", 5); // New relation for comments
    }

    public void generateKgFile(String kgFilePath, String userMapFilePath, String postMapFilePath) {
        try {
            loadMappings(userMapFilePath, postMapFilePath);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(kgFilePath))) {
                List<Question> questions = questionRepository.findAll();
                for (Question question : questions) {
                    String questionId = getPostId(question.getId());
                    writeRelations(writer, question.getLikedBy(), questionId, "likes");
                    writeRelations(writer, question.getDislikedBy(), questionId, "dislikes");
                    writeRelations(writer, question.getSharedBy(), questionId, "shares");
                    writeRelations(writer, question.getComments(), questionId, "commented");
                    if (question.getOriginalUserId() != null) {
                        String originalUserId = getUserId(question.getOriginalUserId());
                        writer.write(originalUserId + "\t" + relationMapping.get("original_posted") + "\t" + questionId + "\n");
                    }

                    UserDTO userDTO = userAuthClient.getUserDetails(question.getUserId());
                    writeRelations(writer, userDTO.getFollowers(), getUserId(userDTO.getId()), "follows");
                }
            }

            saveMappings(userMapFilePath, postMapFilePath);

        } catch (IOException e) {
            log.error("Error generating knowledge graph file: {}", kgFilePath, e);
        }
    }

    private void writeRelations(BufferedWriter writer, Set<String> users, String entityId, String relation) throws IOException {
        int relationIndex = relationMapping.get(relation);
        for (String userId : users) {
            writer.write(getUserId(userId) + "\t" + relationIndex + "\t" + entityId + "\n");
        }
    }

    private void writeRelations(BufferedWriter writer, List<String> following, String userId, String relation) throws IOException {
        int relationIndex = relationMapping.get(relation);
        for (String followedUserId : following) {
            writer.write(getUserId(userId) + "\t" + relationIndex + "\t" + getUserId(followedUserId) + "\n");
        }
    }

    private String getUserId(String originalId) {
        return userMapping.computeIfAbsent(originalId, k -> userIdCounter++).toString();
    }

    private String getPostId(String originalId) {
        return postMapping.computeIfAbsent(originalId, k -> postIdCounter++).toString();
    }

    private void saveMappings(String userMapFilePath, String postMapFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (FileWriter userFileWriter = new FileWriter(userMapFilePath);
             FileWriter postFileWriter = new FileWriter(postMapFilePath)) {
            mapper.writeValue(userFileWriter, userMapping);
            mapper.writeValue(postFileWriter, postMapping);
        }
    }

    private void loadMappings(String userMapFilePath, String postMapFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        try (FileReader userFileReader = new FileReader(userMapFilePath)) {
            userMapping = mapper.readValue(userFileReader, ConcurrentHashMap.class);
            userIdCounter = userMapping.values().stream().max(Integer::compareTo).orElse(0) + 1;
        } catch (IOException e) {
            log.warn("User mapping file not found or empty, starting fresh: {}", userMapFilePath);
            userMapping = new ConcurrentHashMap<>();
            userIdCounter = 1;
        }
        try (FileReader postFileReader = new FileReader(postMapFilePath)) {
            postMapping = mapper.readValue(postFileReader, ConcurrentHashMap.class);
            postIdCounter = postMapping.values().stream().max(Integer::compareTo).orElse(0) + 1;
        } catch (IOException e) {
            log.warn("Post mapping file not found or empty, starting fresh: {}", postMapFilePath);
            postMapping = new ConcurrentHashMap<>();
            postIdCounter = 1;
        }
    }
}
