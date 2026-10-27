package com.quizapp.service;

import com.quizapp.dto.*;
import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.exception.BadRequestException;
import com.quizapp.exception.ResourceNotFoundException;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;

    public QuizService(QuestionRepository questionRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all questions for taking the quiz.
     * CRITICAL SECURITY REQUIREMENT:
     * Does NOT expose isCorrect in this DTO so the frontend never receives correct answers.
     */
    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestionsForQuiz() {
        List<Question> questions = questionRepository.findAll();

        return questions.stream().map(q -> {
            List<OptionResponse> optionResponses = q.getOptions().stream()
                    .map(opt -> new OptionResponse(opt.getId(), opt.getOptionText()))
                    .collect(Collectors.toList());

            return new QuestionResponse(q.getId(), q.getQuestionText(), q.getTopic(), optionResponses);
        }).collect(Collectors.toList());
    }

    /**
     * Evaluates quiz submission strictly on the backend.
     * Calculates score, saves quiz attempt, and returns answer review.
     */
    @Transactional
    public QuizResultResponse submitQuiz(String userEmail, QuizSubmitRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        List<Question> questions = questionRepository.findAll();
        if (questions.isEmpty()) {
            throw new BadRequestException("No questions are currently available in the quiz.");
        }

        // Map submitted answers by questionId
        Map<Long, Long> submissionMap = new HashMap<>();
        if (request.getAnswers() != null) {
            for (AnswerSubmission ans : request.getAnswers()) {
                if (ans.getQuestionId() != null) {
                    submissionMap.put(ans.getQuestionId(), ans.getSelectedOptionId());
                }
            }
        }

        int score = 0;
        int totalQuestions = questions.size();
        List<QuestionResultResponse> questionResults = new ArrayList<>();

        for (Question q : questions) {
            // Find correct option
            Option correctOption = q.getOptions().stream()
                    .filter(Option::isCorrect)
                    .findFirst()
                    .orElse(null);

            Long rawSelectedOptionId = submissionMap.get(q.getId());
            Long selectedOptionId = null;
            if (rawSelectedOptionId != null) {
                boolean isValidSelection = q.getOptions().stream()
                        .anyMatch(opt -> opt.getId().equals(rawSelectedOptionId));
                if (isValidSelection) {
                    selectedOptionId = rawSelectedOptionId;
                }
            }

            boolean isAnswerCorrect = (selectedOptionId != null &&
                    correctOption != null &&
                    correctOption.getId().equals(selectedOptionId));

            if (isAnswerCorrect) {
                score++;
            }

            // Build detailed option review for after-submission review
            List<OptionReviewDto> optionReviews = q.getOptions().stream()
                    .map(opt -> new OptionReviewDto(opt.getId(), opt.getOptionText(), opt.isCorrect()))
                    .collect(Collectors.toList());

            questionResults.add(new QuestionResultResponse(
                    q.getId(),
                    q.getQuestionText(),
                    q.getTopic(),
                    selectedOptionId,
                    correctOption != null ? correctOption.getId() : null,
                    isAnswerCorrect,
                    optionReviews
            ));
        }

        double percentage = totalQuestions > 0 ?
                Math.round(((double) score / totalQuestions) * 1000.0) / 10.0 : 0.0;

        LocalDateTime now = LocalDateTime.now();
        QuizAttempt attempt = new QuizAttempt(user, score, totalQuestions, percentage, now);
        attempt = quizAttemptRepository.save(attempt);

        return new QuizResultResponse(
                attempt.getId(),
                score,
                totalQuestions,
                percentage,
                attempt.getSubmittedAt(),
                questionResults
        );
    }

    /**
     * Retrieves the quiz attempt history for the authenticated user only.
     */
    @Transactional(readOnly = true)
    public List<QuizAttemptResponse> getUserQuizHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderBySubmittedAtDesc(user.getId());

        return attempts.stream().map(a -> new QuizAttemptResponse(
                a.getId(),
                a.getScore(),
                a.getTotalQuestions(),
                a.getPercentage(),
                a.getSubmittedAt()
        )).collect(Collectors.toList());
    }
}
