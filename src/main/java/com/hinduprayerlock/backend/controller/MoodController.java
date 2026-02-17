package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.Sholak;
import com.hinduprayerlock.backend.service.MoodService;
import com.hinduprayerlock.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        userService.getOrCreateUser(user);

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

    @PostMapping("/like/{sholakId}")
    public ResponseEntity<String> likeSholak(
            @AuthenticationPrincipal AuthUser user,
            @PathVariable Long sholakId) {

        UUID userId = UUID.fromString(user.getUserId());

        moodService.likeSholak(userId, sholakId);
        return ResponseEntity.ok("Sholak liked successfully");
    }

    // ✅ 5. Unlike Sholak
    @DeleteMapping("/{userId}/like/{sholakId}")
    public ResponseEntity<String> unlikeSholak(
            @PathVariable UUID userId,
            @PathVariable Long sholakId) {

        moodService.unlikeSholak(userId, sholakId);
        return ResponseEntity.ok("Sholak unliked successfully");
    }

    // ✅ 6. Get All Liked Sholaks
    @GetMapping("/{userId}/liked")
    public ResponseEntity<List<Sholak>> getLikedSholaks(
            @PathVariable UUID userId) {

        List<Sholak> likedSholaks = moodService.getLikedSholaks(userId);
        return ResponseEntity.ok(likedSholaks);
    }
}
