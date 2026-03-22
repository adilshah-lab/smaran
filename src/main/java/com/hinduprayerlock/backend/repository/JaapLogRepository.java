package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.JaapLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface JaapLogRepository extends JpaRepository<JaapLog, Long> {

    Optional<JaapLog> findByUserIdAndJaapDate(String userId, LocalDate date);
}
