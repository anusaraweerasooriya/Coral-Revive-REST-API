package com.project.microservices.auth_service.controller;

import com.project.microservices.auth_service.jwt.JwtUtil;
import com.project.microservices.auth_service.model.User;
import com.project.microservices.auth_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user-service")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/follow/{userIdToFollow}")
    public ResponseEntity<User> followUser(@RequestHeader("Authorization") String token, @PathVariable String userIdToFollow) {
        String userId = extractUserIdFromToken(token);
        User updatedUser = userService.followUser(userId, userIdToFollow);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); 
        }
    }

    @PostMapping("/unfollow/{userIdToUnfollow}")
    public ResponseEntity<User> unfollowUser(@RequestHeader("Authorization") String token, @PathVariable String userIdToUnfollow) {
        String userId = extractUserIdFromToken(token);
        User updatedUser = userService.unfollowUser(userId, userIdToUnfollow);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); 
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserDetails(@PathVariable String userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        return userOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    private String extractUserIdFromToken(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUserId(jwtToken);
    }
}
