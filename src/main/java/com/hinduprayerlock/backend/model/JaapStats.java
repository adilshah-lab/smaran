package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "jaap_stats")
public class JaapStats {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private int totalJaap = 0;

    @Column(nullable = false)
    private int todayJaap = 0;

    // 🔥 ADD THESE (THIS FIXES YOUR ERROR)
    @Column(nullable = false)
    private int streakDays = 0;

    private LocalDate lastActiveDate;
}