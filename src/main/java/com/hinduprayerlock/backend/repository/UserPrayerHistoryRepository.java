package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.UserPrayerHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPrayerHistoryRepository
        extends JpaRepository<UserPrayerHistory, UUID> {
}
