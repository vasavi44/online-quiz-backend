package com.quizapp.service;

import com.quizapp.dto.AuthResponse;
import com.quizapp.dto.LoginRequest;
import com.quizapp.dto.RegisterRequest;
import com.quizapp.dto.UserProfileResponse;
import com.quizapp.entity.User;
import com.quizapp.exception.BadRequestException;
import com.quizapp.exception.ResourceNotFoundException;
import com.quizapp.repository.UserRepository;
import com.quizapp.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String trimmedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(trimmedEmail)) {
            throw new BadRequestException("An account with email " + trimmedEmail + " already exists.");
        }

        User user = new User(
                request.getName().trim(),
                trimmedEmail,
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId());

        return new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        String trimmedEmail = request.getEmail().trim().toLowerCase();

        // This will authenticate through Spring Security and throw BadCredentialsException if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(trimmedEmail, request.getPassword())
        );

        User user = userRepository.findByEmail(trimmedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + trimmedEmail));

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }
}
