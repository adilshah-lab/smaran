package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.dto.ChapterResponse;
import com.hinduprayerlock.backend.ai.dto.NextVerseResponse;
import com.hinduprayerlock.backend.ai.dto.VerseResponse;
import com.hinduprayerlock.backend.model.Chapter;
import com.hinduprayerlock.backend.model.SavedVerse;
import com.hinduprayerlock.backend.model.UserProgress;
import com.hinduprayerlock.backend.model.Verse;
import com.hinduprayerlock.backend.repository.ChapterRepository;
import com.hinduprayerlock.backend.repository.SavedVerseRepository;
import com.hinduprayerlock.backend.repository.UserProgressRepository;
import com.hinduprayerlock.backend.repository.VerseRepository;
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

        UserProgress progress = progressRepository
                .findTopByUserIdOrderByChapterNumberDesc(userId)
                .orElse(null);

        int chapter = 1;
        int lastVerse = 0;
        boolean isNewChapter = false;

        if (progress != null) {
            chapter = progress.getChapterNumber();
            lastVerse = progress.getLastReadVerse();
        }

        Chapter ch = chapterRepository
                .findByChapterNumber(chapter)
                .orElseThrow();

        if (lastVerse >= ch.getVersesCount()) {
            chapter++;
            lastVerse = 0;
            isNewChapter = true;

            if (chapter > 18) return null;
        }

        Verse verse = verseRepository
                .findFirstByChapterNumberAndVerseNumberGreaterThanOrderByVerseNumberAsc(
                        chapter, lastVerse
                )
                .orElseThrow();

        return new NextVerseResponse(
                chapter,
                verse.getVerseNumber(),
                verse.getVerseLabel(),
                verse.getSanskrit(),
                verse.getHindi(),
                verse.getEnglish(),
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

        boolean exists = savedVerseRepository
                .existsByUserIdAndChapterNumberAndVerseNumber(userId, chapter, verse);

        if (!exists) {
            SavedVerse sv = new SavedVerse();
            sv.setUserId(userId);
            sv.setChapterNumber(chapter);
            sv.setVerseNumber(verse);

            savedVerseRepository.save(sv);
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
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(sv -> {

                    Verse v = verseRepository
                            .findByChapterNumberAndVerseNumber(
                                    sv.getChapterNumber(),
                                    sv.getVerseNumber()
                            )
                            .orElseThrow();

                    return new NextVerseResponse(
                            v.getChapterNumber(),
                            v.getVerseNumber(),
                            v.getVerseLabel(),
                            v.getSanskrit(),
                            v.getHindi(),
                            v.getEnglish(),
                            false
                    );

                }).toList();
    }
}