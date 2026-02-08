package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.AiPrayerService;
import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.PrayerEntity;
import com.hinduprayerlock.backend.model.UserPrayerHistory;
import com.hinduprayerlock.backend.repository.PrayerRepository;
import com.hinduprayerlock.backend.repository.UserPrayerHistoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrayerService {

    private final PrayerRepository prayerRepository;
    private final UserPrayerHistoryRepository historyRepository;
    private final AiPrayerService aiPrayerService;

    @Transactional
    public PrayerEntity getPrayerForUser(UUID userId, Mood mood) {

        // 1️⃣ DB FIRST — check unused prayers
        List<PrayerEntity> unusedPrayers =
                prayerRepository.findUnusedPrayers(mood, userId);

        PrayerEntity selectedPrayer;

        if (!unusedPrayers.isEmpty()) {
            // ✅ DB still has prayers → use them
            selectedPrayer = unusedPrayers.get(0);

        } else {
            // ❌ DB exhausted → call Claude
            selectedPrayer = generateAndStorePrayer(mood);
        }

        // 2️⃣ Mark this prayer as used for this user
        UserPrayerHistory history = new UserPrayerHistory();
        history.setUserId(userId);
        history.setPrayerId(selectedPrayer.getId());
        history.setServedAt(LocalDateTime.now());
        historyRepository.save(history);

        return selectedPrayer;
    }

    // Claude is used ONLY here
    private PrayerEntity generateAndStorePrayer(Mood mood) {

        String response = aiPrayerService.generatePrayerForMood(mood);

        PrayerEntity prayer = new PrayerEntity();
        prayer.setMood(mood);
        prayer.setSanskrit(extractSanskrit(response));
        prayer.setEnglish(extractEnglish(response));
        prayer.setHindi(extractHindi(response));

        return prayerRepository.save(prayer);
    }

    // -------------------------
    // Parsing helpers
    // -------------------------

    private String extractSanskrit(String text) {
        return extractSection(text, "Sanskrit:", "English Meaning:");
    }

    private String extractEnglish(String text) {
        return extractSection(text, "English Meaning:", "Hindi Meaning:");
    }

    private String extractHindi(String text) {
        return extractSection(text, "Hindi Meaning:", null);
    }

    private String extractSection(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        if (startIndex == -1) return "";

        startIndex += start.length();

        int endIndex = (end == null) ? text.length() : text.indexOf(end);
        if (endIndex == -1) endIndex = text.length();

        return text.substring(startIndex, endIndex).trim();
    }
}