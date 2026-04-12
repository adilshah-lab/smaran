package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "daily_user_stats",
        indexes = {
                @Index(name = "idx_user_date", columnList = "userId, date")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "date"})
        }
)
public class DailyUserStats {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int jaapCount = 0;

    @Column(nullable = false)
    private int meditationMinutes = 0;

    @Column(nullable = false)
    private int appUsageMinutes = 0;

    @Column(nullable = false)
    private boolean appOpened = false;
}