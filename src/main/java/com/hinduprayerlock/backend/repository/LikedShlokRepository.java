package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.LikedShlok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface LikedShlokRepository extends JpaRepository<LikedShlok, UUID> {

    boolean existsByUserIdAndShlokId(UUID userId, Integer shlokId);

    List<LikedShlok> findByUserId(UUID userId);

    void deleteByUserIdAndShlokId(UUID userId, Integer shlokId);
}


