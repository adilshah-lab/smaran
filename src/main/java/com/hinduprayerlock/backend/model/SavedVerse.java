package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saved_verses",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"userId", "chapterNumber", "verseNumber"}
        ))
@Data
public class SavedVerse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;
    private int chapterNumber;
    private int verseNumber;

    private LocalDateTime createdAt;
}