package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.UserMoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserMoodRepository
        extends JpaRepository<UserMoodEntity, UUID> {

    Optional<UserMoodEntity> findTopByUserIdOrderBySelectedAtDesc(UUID userId);
}
