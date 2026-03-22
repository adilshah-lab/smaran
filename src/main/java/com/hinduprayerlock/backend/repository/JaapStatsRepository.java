package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.JaapStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JaapStatsRepository extends JpaRepository<JaapStats, UUID> {

    Optional<JaapStats> findByUserId(UUID userId);
}