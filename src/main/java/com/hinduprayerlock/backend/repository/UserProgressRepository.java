package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    Optional<UserProgress> findByUserIdAndChapterNumber(UUID userId, int chapter);

    Optional<UserProgress> findTopByUserIdOrderByChapterNumberDesc(UUID userId);
}
