package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.Sholak;
import com.hinduprayerlock.backend.model.UserMoodEntity;
import com.hinduprayerlock.backend.model.UserSholakLike;
import com.hinduprayerlock.backend.repository.SholakRepository;
import com.hinduprayerlock.backend.repository.UserMoodRepository;
import com.hinduprayerlock.backend.repository.UserSholakLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final UserMoodRepository userMoodRepository;
    private final SholakRepository sholakRepository;
    private final UserSholakLikeRepository userSholakLikeRepository;

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

    public Sholak getSholakForCurrentMood(UUID userId) {

        Mood mood = userMoodRepository
                .findTopByUserIdOrderBySelectedAtDesc(userId)
                .map(UserMoodEntity::getMood)
                .orElseThrow(() -> new RuntimeException("Mood not found"));

        // ✅ Correct way: fetch by enum
        List<Sholak> sholaks = sholakRepository.findByMood(mood);

        if (sholaks.isEmpty()) {
            throw new RuntimeException("No sholaks found for mood: " + mood);
        }

        // ✅ Better random generator (thread-safe)
        int randomIndex = ThreadLocalRandom.current().nextInt(sholaks.size());

        return sholaks.get(randomIndex);
    }

    public void likeSholak(UUID userId, Long sholakId) {

        boolean alreadyLiked = userSholakLikeRepository
                .findByUserIdAndSholakId(userId, sholakId)
                .isPresent();

        if (alreadyLiked) {
            return; // already liked
        }

        Sholak sholak = sholakRepository.findById(sholakId)
                .orElseThrow(() -> new RuntimeException("Sholak not found"));

        UserSholakLike like = UserSholakLike.builder()
                .userId(userId)
                .sholak(sholak)
                .likedAt(LocalDateTime.now())
                .build();

        userSholakLikeRepository.save(like);
    }

    public void unlikeSholak(UUID userId, Long sholakId) {
        userSholakLikeRepository.deleteByUserIdAndSholakId(userId, sholakId);
    }

    public List<Sholak> getLikedSholaks(UUID userId) {

        return userSholakLikeRepository.findByUserId(userId)
                .stream()
                .map(UserSholakLike::getSholak)
                .toList();
    }
}
