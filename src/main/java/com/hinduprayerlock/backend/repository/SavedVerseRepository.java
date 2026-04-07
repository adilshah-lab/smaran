package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.SavedVerse;
import com.hinduprayerlock.backend.model.Verse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query("""
    SELECT v FROM Verse v
    JOIN SavedVerse sv
    ON v.chapterNumber = sv.chapterNumber 
    AND v.verseNumber = sv.verseNumber
    WHERE sv.userId = :userId
    ORDER BY sv.createdAt DESC
    """)
    List<Verse> findSavedVersesWithDetails(UUID userId);
}