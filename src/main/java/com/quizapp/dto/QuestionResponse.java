package com.quizapp.dto;

import java.util.List;

public class QuestionResponse {

    private Long id;
    private String questionText;
    private String topic;
    private List<OptionResponse> options;

    public QuestionResponse() {
    }

    public QuestionResponse(Long id, String questionText, String topic, List<OptionResponse> options) {
        this.id = id;
        this.questionText = questionText;
        this.topic = topic;
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<OptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<OptionResponse> options) {
        this.options = options;
    }
}
