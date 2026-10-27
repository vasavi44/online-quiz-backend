package com.quizapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.dto.AnswerSubmission;
import com.quizapp.dto.LoginRequest;
import com.quizapp.dto.QuizSubmitRequest;
import com.quizapp.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuizIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void completeUserQuizFlow() throws Exception {
        String testEmail = "student_" + System.currentTimeMillis() + "@example.com";

        // 1. Unauthenticated request to /api/quiz/questions should be rejected
        mockMvc.perform(get("/api/quiz/questions"))
                .andExpect(status().isForbidden());

        // 2. Register new student
        RegisterRequest registerReq = new RegisterRequest("Student One", testEmail, "pass123456");
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode regNode = objectMapper.readTree(regResult.getResponse().getContentAsString());
        String token = regNode.get("token").asText();
        assertNotNull(token);

        // 3. Login with registered student
        LoginRequest loginReq = new LoginRequest(testEmail, "pass123456");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String authToken = "Bearer " + loginNode.get("token").asText();

        // 4. Retrieve Questions with Bearer Token
        MvcResult questionsResult = mockMvc.perform(get("/api/quiz/questions")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode questionsNode = objectMapper.readTree(questionsResult.getResponse().getContentAsString());
        assertTrue(questionsNode.isArray());
        assertTrue(questionsNode.size() >= 5, "Must display at least 5 questions");

        // CRITICAL CHECK: Verify 'isCorrect' is NEVER exposed in the question responses
        for (JsonNode qNode : questionsNode) {
            JsonNode optionsNode = qNode.get("options");
            assertNotNull(optionsNode);
            for (JsonNode optNode : optionsNode) {
                assertNull(optNode.get("isCorrect"), "CRITICAL: isCorrect must NEVER be exposed before submission!");
            }
        }

        // 5. Submit Quiz with answers for the first two questions
        Long q1Id = questionsNode.get(0).get("id").asLong();
        Long opt1Id = questionsNode.get(0).get("options").get(0).get("id").asLong();

        List<AnswerSubmission> answers = new ArrayList<>();
        answers.add(new AnswerSubmission(q1Id, opt1Id));

        QuizSubmitRequest submitReq = new QuizSubmitRequest(answers);

        MvcResult submitResult = mockMvc.perform(post("/api/quiz/submit")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode submitNode = objectMapper.readTree(submitResult.getResponse().getContentAsString());
        assertNotNull(submitNode.get("score"));
        assertNotNull(submitNode.get("totalQuestions"));
        assertNotNull(submitNode.get("percentage"));
        assertTrue(submitNode.get("questionResults").size() >= 5);

        // In after-submission results, correct answer ID is now returned
        JsonNode firstResult = submitNode.get("questionResults").get(0);
        assertNotNull(firstResult.get("correctOptionId"));

        // 6. Check Quiz History
        MvcResult historyResult = mockMvc.perform(get("/api/quiz/history")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode historyNode = objectMapper.readTree(historyResult.getResponse().getContentAsString());
        assertTrue(historyNode.isArray());
        assertTrue(historyNode.size() >= 1);
        assertEquals(submitNode.get("score").asInt(), historyNode.get(0).get("score").asInt());
    }
}
