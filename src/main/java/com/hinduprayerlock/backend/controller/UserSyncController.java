package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.dto.UserStateResponse;
import com.hinduprayerlock.backend.model.dto.UserSyncRequest;
import com.hinduprayerlock.backend.service.UserSyncService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserSyncController {

    private final UserSyncService service;

    public UserSyncController(UserSyncService service) {
        this.service = service;
    }

    // 🔥 SYNC API
    @PostMapping("/sync")
    public void sync(
            Authentication authentication,
            @RequestBody UserSyncRequest request
    ) {

        UUID userId = UUID.fromString(authentication.getName());

        service.syncUserData(userId, request);
    }

    // 🔥 STATE API
    @GetMapping("/state")
    public UserStateResponse getState(Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        return service.getUserState(userId);
    }
}