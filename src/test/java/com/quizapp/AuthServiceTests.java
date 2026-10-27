package com.quizapp;

import com.quizapp.dto.AuthResponse;
import com.quizapp.dto.RegisterRequest;
import com.quizapp.entity.User;
import com.quizapp.exception.BadRequestException;
import com.quizapp.repository.UserRepository;
import com.quizapp.security.JwtUtil;
import com.quizapp.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtUtil jwtUtil;

    private AuthService authService;

    @org.junit.jupiter.api.BeforeEach
    void init() {
        jwtUtil = new JwtUtil("online-quiz-application-super-secure-jwt-secret-key-32-chars-long!", 86400000);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil, null);
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("Bob", "bob@example.com", "password123");

        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(5L);
            return u;
        });
        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertTrue(jwtUtil.validateToken(response.getToken()));
        assertEquals("bob@example.com", response.getEmail());
        assertEquals("Bob", response.getName());
    }

    @Test
    void register_duplicateEmail_throwsBadRequestException() {
        RegisterRequest request = new RegisterRequest("Bob", "bob@example.com", "password123");

        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }
}
