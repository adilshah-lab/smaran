package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.service.JaapService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracking/jaap")
public class JaapController {

    private final JaapService service;

    public JaapController(JaapService service) {
        this.service = service;
    }

    @PostMapping
    public void addJaap(
            @RequestParam Integer count,
            Authentication auth
    ){
        String userId = auth.getName();
        service.addJaap(userId, count);
    }

    @GetMapping("/today")
    public Integer getTodayJaap(Authentication auth) {

        String userId = auth.getName(); // or extract from JWT
        return service.getToday(userId);
    }
}
