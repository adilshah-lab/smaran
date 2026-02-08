package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity getOrCreateUser(AuthUser authUser) {

        UUID userId = UUID.fromString(authUser.getUserId());

        return userRepository.findById(userId)
                .orElseGet(() -> {
                    UserEntity user = new UserEntity();
                    user.setId(userId);
                    user.setEmail(authUser.getEmail());
                    user.setPassword("JWT_AUTH");
                    user.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }
}
