package com.project.rest.user_recommendation_service.service;

import com.project.rest.user_recommendation_service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserAuthClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String USER_AUTH_SERVICE_URL = "http://auth-service/user-service/user/";

    public UserDTO getUserDetails(String userId) {
        return restTemplate.getForObject(USER_AUTH_SERVICE_URL + userId, UserDTO.class);
    }
}
