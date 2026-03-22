package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mood_config")
@Getter
@Setter
public class MoodConfig {

    @Id
    @Enumerated(EnumType.STRING)
    private Mood mood;

    @Column(nullable = false)
    private boolean active = true;

    private String description;
}
