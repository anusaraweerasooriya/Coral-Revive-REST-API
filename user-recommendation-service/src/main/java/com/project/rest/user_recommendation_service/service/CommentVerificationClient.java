package com.project.rest.user_recommendation_service.service;

import com.project.rest.user_recommendation_service.dto.CommentClassificationRequestDTO;
import com.project.rest.user_recommendation_service.dto.CommentClassificationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommentVerificationClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String FLASK_SERVICE_URL = "http://flask-service/api/comment-verification/classify";

    public String classifyComment(String postContent, String commentContent) {

        CommentClassificationRequestDTO requestDTO = new CommentClassificationRequestDTO(postContent, commentContent);

        CommentClassificationResponseDTO responseDTO = restTemplate.postForObject(FLASK_SERVICE_URL, requestDTO, CommentClassificationResponseDTO.class);

        return responseDTO != null ? responseDTO.getClassification() : "false";
    }
}
