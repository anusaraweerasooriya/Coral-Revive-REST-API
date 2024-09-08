package com.project.rest.user_recommendation_service.service.impl;

import com.project.rest.user_recommendation_service.model.Question;
import com.project.rest.user_recommendation_service.repository.QuestionRepository;
import com.project.rest.user_recommendation_service.service.api.QuestionService;
import com.project.rest.user_recommendation_service.service.PostClassificationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PostClassificationClient postClassificationClient; 


    @Override
    public Question createQuestion(Question question) {
        if (question.getLikedBy() == null) {
            question.setLikedBy(new HashSet<>());
        }
        if (question.getDislikedBy() == null) {
            question.setDislikedBy(new HashSet<>());
        }
        if (question.getSharedBy() == null) {
            question.setSharedBy(new HashSet<>());
        }
        if (question.getComments() == null) {
            question.setComments(new HashSet<>());
        }
        
        // Get categories from the PostClassificationClient
        List<String> categories = postClassificationClient.classifyPost(question.getContent());
        
        // Only add categories if the classification returned non-null and non-empty
        if (categories != null && !categories.isEmpty()) {
            Set<String> categorySet = new HashSet<>(categories);
            question.setCategory(categorySet); 
        } else {
            // Ensure the category field is initialized, even if no categories are returned
            if (question.getCategory() == null) {
                question.setCategory(new HashSet<>());
            }
        }

        // Set other fields
        question.setOriginalUserId(null); 
        question.setRecommended(false);  
        
        // Save the question
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
        List<Question> userPosts = questionRepository.findByUserId(userId);
        return userPosts;
    }

    @Override
    public Question likeQuestion(String questionId, String userId) {
        Question question = getQuestionById(questionId);
        if (question != null) {
            if (question.getLikedBy().contains(userId)) {
                question.getLikedBy().remove(userId);
            } else {
                question.getDislikedBy().remove(userId);
                question.getLikedBy().add(userId);
            }
            return questionRepository.save(question);
        }
        return null;
    }

    @Override
    public Question dislikeQuestion(String questionId, String userId) {
        Question question = getQuestionById(questionId);
        if (question != null) {
            if (question.getDislikedBy().contains(userId)) {
                question.getDislikedBy().remove(userId);
            } else {
                question.getLikedBy().remove(userId);
                question.getDislikedBy().add(userId);
            }
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

    public List<Question> getQuestionsByCategory(String category) {
        return questionRepository.findByCategory(category);
    }

}
