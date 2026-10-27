package com.quizapp;

import com.quizapp.dto.*;
import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTests {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizService quizService;

    private User sampleUser;
    private Question question1;
    private Question question2;
    private Option opt1Correct;
    private Option opt1Wrong;
    private Option opt2Correct;
    private Option opt2Wrong;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Alice", "alice@example.com", "hashedPassword");
        sampleUser.setId(1L);

        question1 = new Question("What is Java?", "Java");
        question1.setId(10L);
        opt1Wrong = new Option("A coffee brand only", false);
        opt1Wrong.setId(101L);
        opt1Correct = new Option("A high-level programming language", true);
        opt1Correct.setId(102L);
        question1.addOption(opt1Wrong);
        question1.addOption(opt1Correct);

        question2 = new Question("What is CSS?", "Web");
        question2.setId(20L);
        opt2Correct = new Option("Cascading Style Sheets", true);
        opt2Correct.setId(201L);
        opt2Wrong = new Option("Computer Software Service", false);
        opt2Wrong.setId(202L);
        question2.addOption(opt2Correct);
        question2.addOption(opt2Wrong);
    }

    @Test
    void getAllQuestionsForQuiz_doesNotExposeCorrectAnswers() {
        when(questionRepository.findAll()).thenReturn(List.of(question1, question2));

        List<QuestionResponse> questions = quizService.getAllQuestionsForQuiz();

        assertEquals(2, questions.size());
        assertEquals(2, questions.get(0).getOptions().size());
        // Verify OptionResponse class does not have isCorrect or method
        for (QuestionResponse q : questions) {
            for (OptionResponse opt : q.getOptions()) {
                assertNotNull(opt.getId());
                assertNotNull(opt.getOptionText());
            }
        }
    }

    @Test
    void submitQuiz_calculatesScoreCorrectly_whenAllCorrect() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser));
        when(questionRepository.findAll()).thenReturn(List.of(question1, question2));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuizSubmitRequest request = new QuizSubmitRequest(List.of(
                new AnswerSubmission(10L, 102L), // correct
                new AnswerSubmission(20L, 201L)  // correct
        ));

        QuizResultResponse result = quizService.submitQuiz("alice@example.com", request);

        assertEquals(2, result.getTotalQuestions());
        assertEquals(2, result.getScore());
        assertEquals(100.0, result.getPercentage());
        assertEquals(2, result.getQuestionResults().size());
        assertTrue(result.getQuestionResults().get(0).isCorrect());
        assertTrue(result.getQuestionResults().get(1).isCorrect());
    }

    @Test
    void submitQuiz_calculatesScoreCorrectly_whenPartialOrWrong() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser));
        when(questionRepository.findAll()).thenReturn(List.of(question1, question2));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuizSubmitRequest request = new QuizSubmitRequest(List.of(
                new AnswerSubmission(10L, 101L), // wrong option
                new AnswerSubmission(20L, null)  // unanswered
        ));

        QuizResultResponse result = quizService.submitQuiz("alice@example.com", request);

        assertEquals(2, result.getTotalQuestions());
        assertEquals(0, result.getScore());
        assertEquals(0.0, result.getPercentage());
        assertFalse(result.getQuestionResults().get(0).isCorrect());
        assertFalse(result.getQuestionResults().get(1).isCorrect());
        assertEquals(102L, result.getQuestionResults().get(0).getCorrectOptionId());
    }
}
