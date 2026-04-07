package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "saved_verses",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "chapter_number", "verse_number"}
        )
)
@Data
public class SavedVerse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "chapter_number", nullable = false)
    private int chapterNumber;

    @Column(name = "verse_number", nullable = false)
    private int verseNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ✅ Auto set timestamp
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}