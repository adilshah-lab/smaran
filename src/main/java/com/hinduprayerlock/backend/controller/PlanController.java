package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.Plan;
import com.hinduprayerlock.backend.service.PlanService;
import com.hinduprayerlock.backend.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final JwtUtil jwtUtil;

    // ✅ Public API
    @GetMapping
    public Object getPlans() {
        return planService.getActivePlans();
    }

    // 🔐 ADMIN ONLY - Create Plan
    @PostMapping
    public Plan createPlan(
            @RequestHeader("Authorization") String token,
            @RequestBody Plan plan
    ) {

        String jwt = token.replace("Bearer ", "");
        String role = jwtUtil.extractRole(jwt);

        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access Denied: Only ADMIN can create plans");
        }

        return planService.createPlan(plan);
    }

    // 🔐 ADMIN ONLY - Update Plan
    @PutMapping("/{id}")
    public Plan updatePlan(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Plan plan
    ) {

        String jwt = token.replace("Bearer ", "");
        String role = jwtUtil.extractRole(jwt);

        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access Denied: Only ADMIN can update plans");
        }

        return planService.updatePlan(id, plan);
    }
}