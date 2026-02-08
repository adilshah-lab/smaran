package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService service;

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        return service.overview();
    }

    @GetMapping("/moods")
    public List<Map<String, Object>> moods() {
        return service.moodStats();
    }
}
