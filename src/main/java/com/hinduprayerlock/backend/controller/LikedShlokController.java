package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.service.LikedShlokService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/liked-shloks")
public class LikedShlokController {

    private final LikedShlokService service;

    public LikedShlokController(LikedShlokService service) {
        this.service = service;
    }

    @PostMapping("/{shlokId}")
    public void likeShlok(
            @RequestParam Integer shlokId,
            Authentication authentication
    ){
        AuthUser user = (AuthUser) authentication.getPrincipal();
        UUID userId = user.getId();

        service.likeShlok(userId, shlokId);
    }

    @DeleteMapping("/{shlokId}")
    public void unlikeShlok(
            @PathVariable Integer shlokId,
            Authentication authentication
    ){
        AuthUser user = (AuthUser) authentication.getPrincipal();
        UUID userId = user.getId();

        service.unlikeShlok(userId, shlokId);
    }

    @GetMapping
    public List<Integer> getLikedShloks(Authentication authentication){

        AuthUser user = (AuthUser) authentication.getPrincipal();
        UUID userId = user.getId();

        return service.getLikedShloks(userId);
    }
}