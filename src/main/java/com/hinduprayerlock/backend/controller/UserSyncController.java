package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.ai.dto.SyncRequest;
import com.hinduprayerlock.backend.service.SyncService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserSyncController {

    private final SyncService syncService;

    public UserSyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/sync")
    public void syncUserData(
            @RequestBody SyncRequest request,
            @RequestHeader("Authorization") String token
    ) {

        syncService.sync(request, token);
    }
}
