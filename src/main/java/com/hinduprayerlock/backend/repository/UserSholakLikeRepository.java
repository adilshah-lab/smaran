package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.UserSholakLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSholakLikeRepository extends JpaRepository<UserSholakLike, Long> {

    Optional<UserSholakLike> findByUserIdAndSholakId(UUID userId, Long shlokId);

    List<UserSholakLike> findByUserId(UUID userId);

    void deleteByUserIdAndSholakId(UUID userId, Long sholakId);
}
