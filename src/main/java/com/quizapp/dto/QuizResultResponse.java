package com.quizapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuizResultResponse {

    private Long attemptId;
    private int score;
    private int totalQuestions;
    private double percentage;
    private LocalDateTime submittedAt;
    private List<QuestionResultResponse> questionResults;

    public QuizResultResponse() {
    }

    public QuizResultResponse(Long attemptId, int score, int totalQuestions,
                              double percentage, LocalDateTime submittedAt,
                              List<QuestionResultResponse> questionResults) {
        this.attemptId = attemptId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.submittedAt = submittedAt;
        this.questionResults = questionResults;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<QuestionResultResponse> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResultResponse> questionResults) {
        this.questionResults = questionResults;
    }
}
