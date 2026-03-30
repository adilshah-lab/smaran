package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.Plan;
import com.hinduprayerlock.backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/plans")
@RequiredArgsConstructor
public class AdminPlanController {

    private final PlanService planService;

    @PostMapping
    public Plan createPlan(@RequestBody Plan plan) {
        return planService.createPlan(plan);
    }

    @GetMapping
    public Object getAllActivePlans() {
        return planService.getActivePlans();
    }
}