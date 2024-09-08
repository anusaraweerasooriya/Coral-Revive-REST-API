package com.project.rest.user_recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PostClassificationResponseDTO {
    
    @JsonProperty("text")
    private String text;

    @JsonProperty("top_two_labels")
    private List<String> topTwoLabels; 

    @JsonProperty("top_two_scores")
    private List<Double> topTwoScores; 

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTopTwoLabels() {
        return topTwoLabels;
    }

    public void setTopTwoLabels(List<String> topTwoLabels) {
        this.topTwoLabels = topTwoLabels;
    }

    public List<Double> getTopTwoScores() {
        return topTwoScores;
    }

    public void setTopTwoScores(List<Double> topTwoScores) {
        this.topTwoScores = topTwoScores;
    }

    @Override
    public String toString() {
        return "PostClassificationResponseDTO{" +
                "text='" + text + '\'' +
                ", top_two_labels=" + topTwoLabels +
                ", top_two_scores=" + topTwoScores +
                '}';
    }
}
