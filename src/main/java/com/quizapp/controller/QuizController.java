package com.quizapp.controller;

import com.quizapp.dto.QuestionResponse;
import com.quizapp.dto.QuizAttemptResponse;
import com.quizapp.dto.QuizResultResponse;
import com.quizapp.dto.QuizSubmitRequest;
import com.quizapp.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions() {
        List<QuestionResponse> questions = quizService.getAllQuestionsForQuiz();
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @Valid @RequestBody QuizSubmitRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        QuizResultResponse result = quizService.submitQuiz(email, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<QuizAttemptResponse>> getHistory(Authentication authentication) {
        String email = authentication.getName();
        List<QuizAttemptResponse> history = quizService.getUserQuizHistory(email);
        return ResponseEntity.ok(history);
    }
}
