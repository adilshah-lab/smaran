package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.MoodConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodConfigRepository extends JpaRepository<MoodConfig, Mood> {
}