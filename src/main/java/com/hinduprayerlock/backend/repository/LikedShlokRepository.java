package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.LikedShlok;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikedShlokRepository extends JpaRepository<LikedShlok, Long> {

    List<LikedShlok> findByUserId(String userId);

    Optional<LikedShlok> findByUserIdAndShlokId(String userId, Integer shlokId);

    void deleteByUserIdAndShlokId(String userId, Integer shlokId);
}
