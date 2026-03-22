package com.hinduprayerlock.backend.repository;


import com.hinduprayerlock.backend.model.AiSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiSettingsRepository extends JpaRepository<AiSettings, Long> {
}