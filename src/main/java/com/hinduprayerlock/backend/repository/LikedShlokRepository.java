package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.LikedShlok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface LikedShlokRepository extends JpaRepository<LikedShlok, UUID> {

    boolean existsByUserIdAndShlokId(Long userId, Integer shlokId);

    List<LikedShlok> findByUserId(Long userId);

    void deleteByUserIdAndShlokId(Long userId, Integer shlokId);

    @Query("SELECT l.shlokId FROM LikedShlok l WHERE l.userId = :userId")
    List<Integer> findShlokIdsByUserId(Long userId);
}


