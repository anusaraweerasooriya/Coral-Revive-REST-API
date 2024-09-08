package com.project.rest.user_recommendation_service.service.impl;

import com.project.rest.user_recommendation_service.model.Question;
import com.project.rest.user_recommendation_service.repository.QuestionRepository;
import com.project.rest.user_recommendation_service.service.api.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public Question createQuestion(Question question) {
        question.setOriginalUserId(null); 
        question.setRecommended(false);  
        return questionRepository.save(question);
    }

    @Override
    public Question getQuestionById(String id) {
        Optional<Question> question = questionRepository.findById(id);
        return question.orElse(null);
    }

    @Override
    public List<Question> getQuestionsByUserId(String userId) {
        return questionRepository.findByUserId(userId);
    }

    @Override
    public List<Question> getQuestionsForFeed(String userId) {
        // Placeholder logic: Extend this to include shared and recommended posts
        List<Question> userPosts = questionRepository.findByUserId(userId);
        // Add logic here to fetch shared posts and recommended posts if needed
        return userPosts;
    }

    @Override
    public Question likeQuestion(String questionId, String userId) {
        Question question = getQuestionById(questionId);
        if (question != null) {
            if (question.getLikedBy().contains(userId)) {
                // User already liked the question, remove the like
                question.getLikedBy().remove(userId);
            } else {
                // If the user has disliked the question, remove the dislike
                question.getDislikedBy().remove(userId);
                // Add the like
                question.getLikedBy().add(userId);
            }
            // Save the updated question
            return questionRepository.save(question);
        }
        return null;
    }

    @Override
    public Question dislikeQuestion(String questionId, String userId) {
        Question question = getQuestionById(questionId);
        if (question != null) {
            if (question.getDislikedBy().contains(userId)) {
                // User already disliked the question, remove the dislike
                question.getDislikedBy().remove(userId);
            } else {
                // If the user has liked the question, remove the like
                question.getLikedBy().remove(userId);
                // Add the dislike
                question.getDislikedBy().add(userId);
            }
            // Save the updated question
            return questionRepository.save(question);
        }
        return null;
    }


    @Override
    public Question shareQuestion(String questionId, String userId) {
        Question question = getQuestionById(questionId);
        if (question != null) {
            Question sharedQuestion = new Question();
            sharedQuestion.setUserId(userId); 
            sharedQuestion.setOriginalUserId(question.getUserId()); 
            sharedQuestion.setContent(question.getContent());
            sharedQuestion.setCreatedAt(new Date());
            sharedQuestion.setSharedBy(question.getSharedBy()); 

            question.getSharedBy().add(userId); 
            questionRepository.save(question); 

            return questionRepository.save(sharedQuestion); 
        }
        return null;
    }

    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }

    public boolean deleteQuestion(String questionId) {
        Question question = getQuestionById(questionId);
        if (question != null) {
            questionRepository.delete(question);
            return true;
        }
        return false;
    }

}
