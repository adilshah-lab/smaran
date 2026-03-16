package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.dto.UserStateResponse;
import com.hinduprayerlock.backend.model.dto.UserSyncRequest;
import org.springframework.web.bind.annotation.*;


import com.hinduprayerlock.backend.service.UserSyncService;

@RestController
@RequestMapping("/api/user")
public class UserSyncController {

    private final UserSyncService service;

    public UserSyncController(UserSyncService service) {
        this.service = service;
    }

    @PostMapping("/sync")
    public void sync(@RequestBody UserSyncRequest request) {
        service.syncUserData(request);
    }

    @GetMapping("/states")
    public UserStateResponse getState(@RequestParam Long userId) {
        return service.getUserState(userId);
    }
}

