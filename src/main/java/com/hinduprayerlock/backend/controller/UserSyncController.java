package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AuthUser;
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

    @PostMapping("/sync")
    public void sync(
            @RequestBody UserSyncRequest request,
            Authentication authentication
    ) {
        AuthUser user = (AuthUser) authentication.getPrincipal();

        UUID userId = UUID.fromString(user.getUserId()); // ✅ FIX

        service.syncUserData(userId, request);
    }

    @GetMapping("/state")
    public UserStateResponse getState(Authentication authentication) {

        AuthUser user = (AuthUser) authentication.getPrincipal();

        UUID userId = UUID.fromString(user.getUserId()); // ✅ FIX

        return service.getUserState(userId);
    }
}