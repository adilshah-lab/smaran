package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.service.MoodService;
import com.hinduprayerlock.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mood")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> setMood(
            @AuthenticationPrincipal AuthUser user,
            @RequestParam Mood mood
    ) {
        userService.getOrCreateUser(user); // ✅ auto-create user

        moodService.setMood(
                UUID.fromString(user.getUserId()),
                mood
        );

        return ResponseEntity.ok(
                Map.of("message", "Mood saved successfully")
        );
    }

    @GetMapping
    public ResponseEntity<?> getMood(
            @AuthenticationPrincipal AuthUser user
    ) {
        userService.getOrCreateUser(user);

        return ResponseEntity.ok(
                Map.of(
                        "mood",
                        moodService.getCurrentMood(
                                UUID.fromString(user.getUserId())
                        )
                )
        );
    }
}
