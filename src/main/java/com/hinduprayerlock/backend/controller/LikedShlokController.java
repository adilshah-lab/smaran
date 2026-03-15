package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.service.LikedShlokService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class LikedShlokController {

    private final LikedShlokService service;

    public LikedShlokController(LikedShlokService service) {
        this.service = service;
    }

    @PostMapping("/liked-shlok")
    public void likeShlok(
            @RequestParam String userId,
            @RequestParam Integer shlokId){

        service.likeShlok(userId, shlokId);
    }

    @DeleteMapping("/liked-shlok/{shlokId}")
    public void unlikeShlok(
            @RequestParam String userId,
            @PathVariable Integer shlokId){

        service.unlikeShlok(userId, shlokId);
    }

    @GetMapping("/liked-shloks")
    public List<Integer> getLikedShloks(
            @RequestParam String userId){

        return service.getLikedShloks(userId);
    }
}
