package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.model.dto.UserStateResponse;
import com.hinduprayerlock.backend.model.dto.UserSyncRequest;
import com.hinduprayerlock.backend.service.UserSyncService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserSyncController {

    private final UserSyncService service;

    public UserSyncController(UserSyncService service) {
        this.service = service;
    }

    // 🔥 SYNC API
    @PostMapping("/sync")
    public void sync(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody UserSyncRequest request
    ) {

        if (user == null || user.getId() == null) {
            throw new RuntimeException("User not authenticated properly");
        }

        UUID userId = user.getId(); // ✅ DIRECT (NO STRING PARSING)

        service.syncUserData(userId, request);
    }

    // 🔥 STATE API
    @GetMapping("/state")
    public UserStateResponse getState(
            @AuthenticationPrincipal AuthUser user
    ) {

        if (user == null || user.getId() == null) {
            throw new RuntimeException("User not authenticated properly");
        }

        UUID userId = user.getId(); // ✅ DIRECT

        return service.getUserState(userId);
    }
}