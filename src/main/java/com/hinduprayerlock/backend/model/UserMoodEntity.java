package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_moods")
public class UserMoodEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mood mood;

    @Column(nullable = false)
    private LocalDateTime selectedAt;

    protected UserMoodEntity() {}

    public UserMoodEntity(UUID userId, Mood mood, LocalDateTime selectedAt) {
        this.userId = userId;
        this.mood = mood;
        this.selectedAt = selectedAt;
    }
}
