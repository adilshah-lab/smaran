package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.dto.RegisterRequest;
import com.hinduprayerlock.backend.exceptions.EmailAlreadyExistsException;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.LoginRequest;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }

        UserEntity user = new UserEntity(
                UUID.randomUUID(),
                request.getUsername(),   // no uniqueness check
                request.getEmail(),
                request.getPhoneNumber(),
                passwordEncoder.encode(request.getPassword()),
                LocalDateTime.now()
        );

        userRepository.save(user);

        return jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                "USER"
        );
    }

    public String login(LoginRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                "USER"
        );
    }
}
