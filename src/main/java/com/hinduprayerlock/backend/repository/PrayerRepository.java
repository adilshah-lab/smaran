package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.PrayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PrayerRepository extends JpaRepository<PrayerEntity, UUID> {

    @Query("""
        SELECT p FROM PrayerEntity p
        WHERE p.mood = :mood
        AND p.id NOT IN (
            SELECT h.prayerId FROM UserPrayerHistory h WHERE h.userId = :userId
        )
    """)
    List<PrayerEntity> findUnusedPrayers(
            @Param("mood") Mood mood,
            @Param("userId") UUID userId
    );

    List<PrayerEntity> findByMood(Mood mood);
}
