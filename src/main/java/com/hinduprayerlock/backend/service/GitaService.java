package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.dto.ChapterResponse;
import com.hinduprayerlock.backend.ai.dto.NextVerseResponse;
import com.hinduprayerlock.backend.ai.dto.VerseResponse;
import com.hinduprayerlock.backend.model.*;
import com.hinduprayerlock.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GitaService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private VerseRepository verseRepository;

    @Autowired
    private UserProgressRepository progressRepository;

    @Autowired
    private SavedVerseRepository savedVerseRepository;

    @Autowired
    private OverlayProgressRepository overlayProgressRepository;


    // ✅ 1. Chapters with progress
    public List<ChapterResponse> getChapters(UUID userId) {

        List<Chapter> chapters = chapterRepository.findAll();

        return chapters.stream().map(ch -> {

            int read = progressRepository
                    .findByUserIdAndChapterNumber(userId, ch.getChapterNumber())
                    .map(UserProgress::getLastReadVerse)
                    .orElse(0);

            return new ChapterResponse(
                    ch.getChapterNumber(),
                    ch.getVersesCount(),
                    read,
                    ch.getImageName()
            );

        }).toList();
    }

    // ✅ 2. Verses with isRead
    public List<VerseResponse> getVerses(UUID userId, int chapter) {

        int lastRead = progressRepository
                .findByUserIdAndChapterNumber(userId, chapter)
                .map(UserProgress::getLastReadVerse)
                .orElse(0);

        return verseRepository
                .findByChapterNumberOrderByVerseNumberAsc(chapter)
                .stream()
                .map(v -> new VerseResponse(
                        v.getVerseNumber(),
                        v.getSanskrit(),
                        v.getVerseNumber() <= lastRead
                ))
                .toList();
    }

    // ✅ 3. Next verse (core logic)
    public NextVerseResponse getNextVerse(UUID userId) {

        OverlayProgress progress = overlayProgressRepository
                .findByUserId(userId)
                .orElse(null);

        int chapter = 1;
        int verse = 1;
        boolean isNewChapter = false;

        if (progress != null) {
            chapter = progress.getCurrentChapter();
            verse = progress.getCurrentVerse();
        }

        Chapter ch = chapterRepository
                .findByChapterNumber(chapter)
                .orElseThrow();

        if (verse >= ch.getVersesCount()) {
            chapter++;
            verse = 1;
            isNewChapter = true;

            if (chapter > 18) {
                throw new RuntimeException("All chapters completed");
            }
        } else {
            verse++;
        }

        // SAVE NEW POSITION
        OverlayProgress newProgress = progress != null ? progress : new OverlayProgress();

        newProgress.setUserId(userId);
        newProgress.setCurrentChapter(chapter);
        newProgress.setCurrentVerse(verse);

        overlayProgressRepository.save(newProgress);

        Verse v = verseRepository
                .findByChapterNumberAndVerseNumber(chapter, verse)
                .orElseThrow();

        return new NextVerseResponse(
                chapter,
                verse,
                v.getVerseLabel(),
                v.getSanskrit(),
                v.getHindi(),
                v.getEnglish(),
                isNewChapter
        );
    }

    // ✅ 4. Mark read
    public void markAsRead(UUID userId, int chapter, int verse) {

        UserProgress progress = progressRepository
                .findByUserIdAndChapterNumber(userId, chapter)
                .orElse(new UserProgress());

        progress.setUserId(userId);
        progress.setChapterNumber(chapter);
        progress.setLastReadVerse(verse);

        progressRepository.save(progress);
    }

    // ✅ Save verse
    public void saveVerse(UUID userId, int chapter, int verse) {

        SavedVerse sv = new SavedVerse();
        sv.setUserId(userId);
        sv.setChapterNumber(chapter);
        sv.setVerseNumber(verse);

        try {
            savedVerseRepository.save(sv);
        } catch (Exception e) {
            // already exists → ignore
        }
    }

    // ✅ Remove saved verse
    public void removeSavedVerse(UUID userId, int chapter, int verse) {
        savedVerseRepository
                .deleteByUserIdAndChapterNumberAndVerseNumber(userId, chapter, verse);
    }

    // ✅ Get all saved verses (FULL DATA)
    public List<NextVerseResponse> getSavedVerses(UUID userId) {

        return savedVerseRepository
                .findSavedVersesWithDetails(userId)
                .stream()
                .map(v -> new NextVerseResponse(
                        v.getChapterNumber(),
                        v.getVerseNumber(),
                        v.getVerseLabel(),
                        v.getSanskrit(),
                        v.getHindi(),
                        v.getEnglish(),
                        false
                ))
                .toList();
    }

    public NextVerseResponse getVerse(UUID userId, int chapter, int verse) {

        Verse v = verseRepository
                .findByChapterNumberAndVerseNumber(chapter, verse)
                .orElseThrow(() -> new RuntimeException("Verse not found"));

        return new NextVerseResponse(
                v.getChapterNumber(),
                v.getVerseNumber(),
                v.getVerseLabel(),
                v.getSanskrit(),
                v.getHindi(),
                v.getEnglish(),
                false
        );
    }
}