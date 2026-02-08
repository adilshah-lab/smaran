package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.UserMoodEntity;
import com.hinduprayerlock.backend.repository.UserMoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final UserMoodRepository userMoodRepository;

    public void setMood(UUID userId, Mood mood) {

        UserMoodEntity entity =
                new UserMoodEntity(userId, mood, LocalDateTime.now());

        userMoodRepository.save(entity);
    }

    public Mood getCurrentMood(UUID userId) {

        return userMoodRepository
                .findTopByUserIdOrderBySelectedAtDesc(userId)
                .map(UserMoodEntity::getMood)
                .orElse(null);
    }
}
