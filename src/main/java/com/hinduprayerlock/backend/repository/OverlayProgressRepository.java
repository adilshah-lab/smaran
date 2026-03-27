package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.OverlayProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OverlayProgressRepository extends JpaRepository<OverlayProgress, UUID> {

    Optional<OverlayProgress> findByUserId(UUID userId);
}
