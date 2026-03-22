package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.dto.AuthResponse;
import com.hinduprayerlock.backend.ai.dto.LoginResponse;
import com.hinduprayerlock.backend.ai.dto.RegisterRequest;
import com.hinduprayerlock.backend.exceptions.EmailAlreadyExistsException;
import com.hinduprayerlock.backend.exceptions.InvalidCredentialsException;
import com.hinduprayerlock.backend.exceptions.PhoneAlreadyExistsException;
import com.hinduprayerlock.backend.exceptions.ResourceNotFoundException;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.LoginRequest;
import com.hinduprayerlock.backend.model.dto.UpdateUserRequest;
import com.hinduprayerlock.backend.model.dto.UserResponse;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new PhoneAlreadyExistsException("Phone number already registered");
        }

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsSubscribed(false);

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                "USER"
        );

        return new AuthResponse(token, user.getUsername(), user.getCreatedAt());
    }

    public LoginResponse login(LoginRequest request) {

        if (request.getIdentifier() == null || request.getIdentifier().isBlank()) {
            throw new InvalidCredentialsException("Identifier is required");
        }

        UserEntity user = userRepository
                .findByEmailOrPhoneNumber(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                "USER"
        );

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail()
        );
    }

    public UserResponse getUser(UUID userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToResponse(user);
    }


    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername().trim());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {

            Optional<UserEntity> existingUser =
                    userRepository.findByPhoneNumber(request.getPhoneNumber());

            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new PhoneAlreadyExistsException("Phone number already in use");
            }

            user.setPhoneNumber(request.getPhoneNumber().trim());
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(UserEntity user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getIsSubscribed(),
                user.getCreatedAt()
        );

    }
}
