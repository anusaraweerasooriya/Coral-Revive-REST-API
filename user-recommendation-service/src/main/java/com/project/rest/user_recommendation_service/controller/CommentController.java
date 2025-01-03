package com.project.rest.user_recommendation_service.controller;

import com.project.rest.user_recommendation_service.dto.UserDTO;
import com.project.rest.user_recommendation_service.model.Comment;
import com.project.rest.user_recommendation_service.model.Question;
import com.project.rest.user_recommendation_service.service.AuthServiceClient;
import com.project.rest.user_recommendation_service.service.api.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.rest.user_recommendation_service.service.UserAuthClient;
import com.project.rest.user_recommendation_service.service.api.QuestionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("user-recommendation-service/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserAuthClient userAuthClient;

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestHeader("Authorization") String token, @RequestBody Comment comment) {
        logger.info("Received POST request to add comment.");
        
        boolean isTokenValid = authServiceClient.validateToken(token);
        logger.info("Token validation result: {}", isTokenValid);
        
        if (!isTokenValid) {
            logger.warn("Unauthorized request with invalid token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        logger.info("Token is valid. Proceeding to add comment.");
        
        try {
            Comment addedComment = commentService.addComment(comment);
            logger.info("Comment added successfully with ID: {}", addedComment.getId());
            Question question = questionService.getQuestionById(addedComment.getQuestionId());
            if (question != null) {
                question.getComments().add(addedComment.getUserId());
                questionService.updateQuestion(question);
                logger.info("Comment associated with Question ID: {}", question.getId());
            } else {
                logger.warn("Question not found for the provided question ID: {}", addedComment.getQuestionId());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(addedComment);
        } catch (Exception e) {
            logger.error("Error occurred while adding comment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        Comment comment = commentService.getCommentById(id);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Map<String, Object>>> getCommentsByQuestionId(@PathVariable String questionId) {
    List<Comment> comments = commentService.getCommentsByQuestionId(questionId);
    List<Map<String, Object>> commentsWithUser = new ArrayList<>();

    for (Comment comment : comments) {
        UserDTO userDTO = userAuthClient.getUserDetails(comment.getUserId());
        Map<String, Object> commentWithUser = new HashMap<>();
        commentWithUser.put("comment", comment);
        commentWithUser.put("user", userDTO);
        commentsWithUser.add(commentWithUser);
    }

    return ResponseEntity.ok(commentsWithUser);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(@PathVariable String userId) {
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }
}
