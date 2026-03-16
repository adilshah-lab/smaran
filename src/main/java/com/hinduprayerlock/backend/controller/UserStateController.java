package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.dto.UserStateResponse;
import com.hinduprayerlock.backend.service.UserStateService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserStateController {

    private final UserStateService userStateService;

    public UserStateController(UserStateService userStateService) {
        this.userStateService = userStateService;
    }

    @GetMapping("/state")
    public UserStateResponse getState(Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        return userStateService.getUserState(userId);
    }
}