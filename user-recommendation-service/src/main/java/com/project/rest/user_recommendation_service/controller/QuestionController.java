package com.project.rest.user_recommendation_service.controller;

import com.project.rest.user_recommendation_service.dto.UserDTO;
import com.project.rest.user_recommendation_service.model.Question;
import com.project.rest.user_recommendation_service.service.AuthServiceClient;
import com.project.rest.user_recommendation_service.service.api.QuestionService;
import com.project.rest.user_recommendation_service.service.impl.InteractionDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.rest.user_recommendation_service.service.UserAuthClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AuthServiceClient authServiceClient;


    @Autowired
    private UserAuthClient userAuthClient;

    // Endpoint to create a new question
    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestHeader("Authorization") String token,
                                               @RequestBody Question question) {
    System.out.println("Received request to create a question.");
    
    boolean isTokenValid = authServiceClient.validateToken(token);
    if (!isTokenValid) {
        System.out.println("Token validation failed.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    
    System.out.println("Token validation passed.");
    Question createdQuestion = questionService.createQuestion(question);
    System.out.println("Question created: " + createdQuestion.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }


    // Endpoint to get a specific question by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuestionById(@PathVariable String id) {
        Question question = questionService.getQuestionById(id);
        if (question != null) {
            // Get user information based on userId in the question
            UserDTO userDTO = userAuthClient.getUserDetails(question.getUserId());

            // Prepare the response map
            Map<String, Object> response = new HashMap<>();
            response.put("question", question);
            response.put("user", userDTO);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@RequestHeader("Authorization") String token,
                                               @PathVariable String id) {
        boolean isTokenValid = authServiceClient.validateToken(token);
        if (!isTokenValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        boolean isDeleted = questionService.deleteQuestion(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@RequestHeader("Authorization") String token,
                                                   @PathVariable String id,
                                                   @RequestBody Question updatedQuestion) {
        boolean isTokenValid = authServiceClient.validateToken(token);
        if (!isTokenValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        updatedQuestion.setId(id);
        Question question = questionService.updateQuestion(updatedQuestion);
        if (question != null) {
            return ResponseEntity.ok(question);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Endpoint to get questions by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Question>> getQuestionsByUserId(@PathVariable String userId) {
        List<Question> questions = questionService.getQuestionsByUserId(userId);
        return ResponseEntity.ok(questions);
    }

    // Endpoint to like a question
    @PostMapping("/{id}/like")
    public ResponseEntity<Question> likeQuestion(@RequestHeader("Authorization") String token,
                                                 @RequestBody Question question,
                                                 @PathVariable String id) {
        boolean isTokenValid = authServiceClient.validateToken(token);
        if (!isTokenValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Question likedQuestion = questionService.likeQuestion(id, question.getUserId());
        if (likedQuestion != null) {
            return ResponseEntity.ok(likedQuestion);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<Question> dislikeQuestion(@RequestHeader("Authorization") String token,
                                                    @RequestBody Question question,
                                                    @PathVariable String id) {
        boolean isTokenValid = authServiceClient.validateToken(token);
        if (!isTokenValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Question dislikedQuestion = questionService.dislikeQuestion(id, question.getUserId());
        if (dislikedQuestion != null) {
            return ResponseEntity.ok(dislikedQuestion);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Question> shareQuestion(@RequestHeader("Authorization") String token,
                                                  @RequestBody Question question,
                                                  @PathVariable String id) {
        boolean isTokenValid = authServiceClient.validateToken(token);
        if (!isTokenValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Question sharedQuestion = questionService.shareQuestion(id, question.getUserId());
        if (sharedQuestion != null) {
            return ResponseEntity.ok(sharedQuestion);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/feed/{userId}")
    public ResponseEntity<List<Question>> getQuestionsForFeed(@PathVariable String userId) {
        List<Question> feedQuestions = questionService.getQuestionsForFeed(userId);
        return ResponseEntity.ok(feedQuestions);
    }
}
