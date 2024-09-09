package com.project.rest.user_recommendation_service.service.api;

import com.project.rest.user_recommendation_service.model.Question;

import java.util.List;

public interface QuestionService {
    Question createQuestion(Question question);
    Question getQuestionById(String id);
    List<Question> getQuestionsByUserId(String userId);
    List<Question> getQuestionsForFeed(String userId);
    Question likeQuestion(String questionId, String userId);
    Question dislikeQuestion(String questionId, String userId);
    Question shareQuestion(String questionId, String userId);
    Question updateQuestion(Question question);
    boolean deleteQuestion(String questionId);
    List<Question> getQuestionsByCategory(String category);

}
