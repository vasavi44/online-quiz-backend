package com.quizapp.dto;

import java.time.LocalDateTime;

public class QuizAttemptResponse {

    private Long id;
    private int score;
    private int totalQuestions;
    private double percentage;
    private LocalDateTime submittedAt;

    public QuizAttemptResponse() {
    }

    public QuizAttemptResponse(Long id, int score, int totalQuestions, double percentage, LocalDateTime submittedAt) {
        this.id = id;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
