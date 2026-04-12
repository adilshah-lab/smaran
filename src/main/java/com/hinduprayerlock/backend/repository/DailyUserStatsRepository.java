package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.DailyUserStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyUserStatsRepository extends JpaRepository<DailyUserStats, UUID> {

    Optional<DailyUserStats> findByUserIdAndDate(UUID userId, LocalDate date);

    List<DailyUserStats> findByUserIdAndDateBetween(
            UUID userId,
            LocalDate start,
            LocalDate end
    );

    void deleteByDateBefore(LocalDate date);
}
