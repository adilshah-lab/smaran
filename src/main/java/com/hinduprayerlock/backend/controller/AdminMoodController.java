package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.MoodConfig;
import com.hinduprayerlock.backend.service.AdminMoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/moods")
@RequiredArgsConstructor
public class AdminMoodController {

    private final AdminMoodService service;

    @GetMapping
    public List<MoodConfig> getAll() {
        return service.getAll();
    }

    @PutMapping("/{mood}")
    public MoodConfig update(
            @PathVariable Mood mood,
            @RequestParam boolean active,
            @RequestParam(required = false) String description
    ) {
        return service.update(mood, active, description);
    }
}
