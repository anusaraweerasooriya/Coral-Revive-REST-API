package com.project.rest.user_recommendation_service.service;

import com.project.rest.user_recommendation_service.dto.CommentClassificationRequestDTO;
import com.project.rest.user_recommendation_service.dto.CommentClassificationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommentVerificationClient {

    @Autowired
    private RestTemplate loadBalancedRestTemplate;

    private static final String FLASK_SERVICE_URL = "http://127.0.0.1:5000/api/comment-verification/classify";

    public String classifyComment(String postContent, String commentContent) {

        CommentClassificationRequestDTO requestDTO = new CommentClassificationRequestDTO(postContent, commentContent);

        CommentClassificationResponseDTO responseDTO = loadBalancedRestTemplate.postForObject(
            FLASK_SERVICE_URL, requestDTO, CommentClassificationResponseDTO.class);
        return responseDTO != null ? responseDTO.getClassification() : "false";
    }
}
