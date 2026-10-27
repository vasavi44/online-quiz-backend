package com.quizapp.dto;

import java.util.List;

public class QuestionResultResponse {

    private Long questionId;
    private String questionText;
    private String topic;
    private Long selectedOptionId;
    private Long correctOptionId;
    private boolean isCorrect;
    private List<OptionReviewDto> options;

    public QuestionResultResponse() {
    }

    public QuestionResultResponse(Long questionId, String questionText, String topic,
                                  Long selectedOptionId, Long correctOptionId,
                                  boolean isCorrect, List<OptionReviewDto> options) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.topic = topic;
        this.selectedOptionId = selectedOptionId;
        this.correctOptionId = correctOptionId;
        this.isCorrect = isCorrect;
        this.options = options;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public Long getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Long correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        this.isCorrect = correct;
    }

    public List<OptionReviewDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionReviewDto> options) {
        this.options = options;
    }
}
