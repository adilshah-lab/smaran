package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public Object getPlans() {
        return planService.getActivePlans();
    }
}