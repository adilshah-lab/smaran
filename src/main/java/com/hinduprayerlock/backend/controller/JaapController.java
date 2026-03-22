package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.service.JaapService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class JaapController {

    private final JaapService service;

    public JaapController(JaapService service) {
        this.service = service;
    }

    @PostMapping("/jaap")
    public void addJaap(
            @RequestParam String userId,
            @RequestParam Integer count){

        service.addJaap(userId, count);
    }

    @GetMapping("/jaap")
    public Integer getTodayJaap(
            @RequestParam String userId){

        return service.getToday(userId);
    }
}
