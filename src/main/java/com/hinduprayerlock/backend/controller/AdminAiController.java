package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AiSettings;
import com.hinduprayerlock.backend.service.AdminAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ai")
@RequiredArgsConstructor
public class AdminAiController {

    private final AdminAiService service;

    @GetMapping
    public AiSettings get() {
        return service.get();
    }

    @PutMapping
    public AiSettings update(
            @RequestParam boolean enabled,
            @RequestParam int maxCalls
    ) {
        return service.update(enabled, maxCalls);
    }
}
