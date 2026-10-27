package com.quizapp.dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class QuizSubmitRequest {

    @NotNull(message = "Answers list cannot be null")
    private List<AnswerSubmission> answers = new ArrayList<>();

    public QuizSubmitRequest() {
    }

    public QuizSubmitRequest(List<AnswerSubmission> answers) {
        this.answers = answers;
    }

    public List<AnswerSubmission> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerSubmission> answers) {
        this.answers = answers;
    }
}
