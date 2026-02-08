package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.PrayerEntity;
import com.hinduprayerlock.backend.service.PrayerService;
import com.hinduprayerlock.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/prayer")
@RequiredArgsConstructor
public class PrayerController {

    private final PrayerService prayerService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> getPrayer(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody Map<String, String> body
    ) {
        // 1. Ensure user exists
        userService.getOrCreateUser(user);

        // 2. Parse mood
        Mood mood = Mood.valueOf(body.get("mood"));

        // 3. Get prayer (DB-first, AI fallback, no duplicates)
        PrayerEntity prayer = prayerService.getPrayerForUser(
                UUID.fromString(user.getUserId()),
                mood
        );

        // 4. Response
        return ResponseEntity.ok(
                Map.of(
                        "mood", mood.name(),
                        "sanskrit", prayer.getSanskrit(),
                        "english", prayer.getEnglish(),
                        "hindi", prayer.getHindi()
                )
        );
    }
}
