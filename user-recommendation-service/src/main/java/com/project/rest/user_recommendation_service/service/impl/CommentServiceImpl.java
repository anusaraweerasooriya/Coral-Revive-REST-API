package com.project.rest.user_recommendation_service.service.impl;

import com.project.rest.user_recommendation_service.model.Comment;
import com.project.rest.user_recommendation_service.model.Question;
import com.project.rest.user_recommendation_service.repository.CommentRepository;
import com.project.rest.user_recommendation_service.service.CommentVerificationClient;
import com.project.rest.user_recommendation_service.service.api.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.rest.user_recommendation_service.service.api.QuestionService;



import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentVerificationClient commentVerificationClient;

    @Autowired
    private QuestionService questionService;


    @Override
    public Comment addComment(Comment comment) {
        Question question = questionService.getQuestionById(comment.getQuestionId());
        
        if (question == null) {
            throw new IllegalArgumentException("Invalid question ID: " + comment.getQuestionId());
        }
        String verificationResult = commentVerificationClient.classifyComment(question.getContent(), comment.getContent());

        comment.setVerified(verificationResult);

        return commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.orElse(null);
    }

    @Override
    public List<Comment> getCommentsByQuestionId(String questionId) {
        return commentRepository.findByQuestionId(questionId);
    }

    @Override
    public List<Comment> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId);
    }

    // Placeholder method for the fact-checking model
    private String factCheckComment(String content) {
        // TODO: Integrate the actual fact-checking model here
        return "true"; // Placeholder value
    }
}
