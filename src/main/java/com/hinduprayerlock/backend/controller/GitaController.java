package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.ai.dto.ChapterResponse;
import com.hinduprayerlock.backend.ai.dto.NextVerseResponse;
import com.hinduprayerlock.backend.ai.dto.VerseResponse;
import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.service.GitaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/content/gita")
public class GitaController {

    @Autowired
    private GitaService gitaService;

    // ✅ Extract userId from JWT
    private UUID getUserId(Authentication authentication) {
        AuthUser user = (AuthUser) authentication.getPrincipal();
        return user.getId();
    }

    // ================================
    // ✅ 1. Chapters
    // ================================
    @GetMapping("/chapters")
    public List<ChapterResponse> getChapters(Authentication auth) {

        UUID userId = getUserId(auth);

        return gitaService.getChapters(userId);
    }

    // ================================
    // ✅ 2. Verses
    // ================================
    @GetMapping("/chapters/{chapter}/verses")
    public List<VerseResponse> getVerses(
            Authentication auth,
            @PathVariable int chapter
    ) {

        UUID userId = getUserId(auth);

        return gitaService.getVerses(userId, chapter);
    }

    // ================================
    // ✅ 3. Next Verse
    // ================================
    @GetMapping("/next")
    public NextVerseResponse getNext(Authentication auth) {

        UUID userId = getUserId(auth);

        return gitaService.getNextVerse(userId);
    }

    // ================================
    // ✅ 4. Mark Read
    // ================================
    @PostMapping("/mark-read")
    public String markRead(
            Authentication auth,
            @RequestParam int chapter,
            @RequestParam int verse
    ) {

        UUID userId = getUserId(auth);

        gitaService.markAsRead(userId, chapter, verse);

        return "Done";
    }

    // ================================
    // ✅ 5. Save Verse
    // ================================
    @PostMapping("/save")
    public String saveVerse(
            Authentication auth,
            @RequestParam int chapter,
            @RequestParam int verse
    ) {

        UUID userId = getUserId(auth);

        gitaService.saveVerse(userId, chapter, verse);

        return "Saved";
    }

    // ================================
    // ✅ 6. Remove Saved
    // ================================
    @DeleteMapping("/save")
    public String removeSaved(
            Authentication auth,
            @RequestParam int chapter,
            @RequestParam int verse
    ) {

        UUID userId = getUserId(auth);

        gitaService.removeSavedVerse(userId, chapter, verse);

        return "Removed";
    }

    // ================================
    // ✅ 7. Get Saved
    // ================================
    @GetMapping("/saved")
    public List<NextVerseResponse> getSaved(Authentication auth) {

        UUID userId = getUserId(auth);

        return gitaService.getSavedVerses(userId);
    }

    // ================================
    // ✅ 8. Get Verse by Chapter + Verse
    // ================================
    @GetMapping("/verse")
    public NextVerseResponse getVerse(
            Authentication auth,
            @RequestParam int chapter,
            @RequestParam int verse
    ) {

        UUID userId = getUserId(auth);

        return gitaService.getVerse(userId, chapter, verse);
    }
}