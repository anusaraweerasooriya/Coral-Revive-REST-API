package com.project.rest.user_recommendation_service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rest.user_recommendation_service.model.Question;
import com.project.rest.user_recommendation_service.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InteractionDataService {

    @Autowired
    private QuestionRepository questionRepository;

    private Map<String, Integer> userMapping = new HashMap<>();
    private Map<String, Integer> itemMapping = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    public void generateRatingsFile(String filePath, String userMapFilePath, String itemMapFilePath) {
        loadMappings(userMapFilePath, itemMapFilePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            List<Question> questions = questionRepository.findAll();

            for (Question question : questions) {
                String questionId = question.getId();

                int itemIndex = getOrCreateItemIndex(questionId);

                for (String userId : question.getLikedBy()) {
                    int userIndex = getOrCreateUserIndex(userId);
                    writer.write(userIndex + "\t" + itemIndex + "\t1\n");
                }

                for (String userId : question.getDislikedBy()) {
                    int userIndex = getOrCreateUserIndex(userId);
                    writer.write(userIndex + "\t" + itemIndex + "\t0\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveMappings(userMapFilePath, itemMapFilePath);
    }

    private void loadMappings(String userMapFilePath, String itemMapFilePath) {
        try {
            File userMapFile = new File(userMapFilePath);
            File itemMapFile = new File(itemMapFilePath);

            if (userMapFile.exists()) {
                userMapping = objectMapper.readValue(userMapFile, new TypeReference<Map<String, Integer>>() {});
            }

            if (itemMapFile.exists()) {
                itemMapping = objectMapper.readValue(itemMapFile, new TypeReference<Map<String, Integer>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMappings(String userMapFilePath, String itemMapFilePath) {
        try {
            objectMapper.writeValue(new File(userMapFilePath), userMapping);
            objectMapper.writeValue(new File(itemMapFilePath), itemMapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getOrCreateUserIndex(String userId) {
        return userMapping.computeIfAbsent(userId, id -> userMapping.size());
    }

    private int getOrCreateItemIndex(String itemId) {
        return itemMapping.computeIfAbsent(itemId, id -> itemMapping.size());
    }
}
