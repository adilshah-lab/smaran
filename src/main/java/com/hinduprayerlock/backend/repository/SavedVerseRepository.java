package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.SavedVerse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SavedVerseRepository extends JpaRepository<SavedVerse, Long> {

    boolean existsByUserIdAndChapterNumberAndVerseNumber(
            UUID userId, int chapter, int verse
    );

    void deleteByUserIdAndChapterNumberAndVerseNumber(
            UUID userId, int chapter, int verse
    );

    List<SavedVerse> findByUserIdOrderByCreatedAtDesc(UUID userId);
}