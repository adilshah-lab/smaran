package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.LikedShlok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikedShlokRepository extends JpaRepository<LikedShlok, UUID> {

    List<LikedShlok> findByUserId(Long userId);

    Optional<LikedShlok> findByUserIdAndShlokId(Long userId, Integer shlokId);

    void deleteByUserIdAndShlokId(Long userId , Integer shlokId);

    @Query("SELECT l.shlokId FROM LikedShlok l WHERE l.userId = :userId")
    List<Integer> findShlokIdsByUserId(Long userId);

    void deleteByUserId(Long userId);
}

