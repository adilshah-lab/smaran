package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.Plan;
import com.hinduprayerlock.backend.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    public Plan createPlan(Plan plan) {
        return planRepository.save(plan);
    }

    public List<Plan> getActivePlans() {
        return planRepository.findByActiveTrue();
    }

    public Plan getPlanById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
    }

    public Plan updatePlan(Long id, Plan updatedPlan) {

        Plan existingPlan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // ✅ Update only your fields
        if (updatedPlan.getName() != null) {
            existingPlan.setName(updatedPlan.getName());
        }

        if (updatedPlan.getDescription() != null) {
            existingPlan.setDescription(updatedPlan.getDescription());
        }

        if (updatedPlan.getPrice() != null) {
            existingPlan.setPrice(updatedPlan.getPrice());
        }

        if (updatedPlan.getDurationInDays() != null) {
            existingPlan.setDurationInDays(updatedPlan.getDurationInDays());
        }

        if (updatedPlan.getActive() != null) {
            existingPlan.setActive(updatedPlan.getActive());
        }

        if (updatedPlan.getGoogleProductId() != null) {
            existingPlan.setGoogleProductId(updatedPlan.getGoogleProductId());
        }

        return planRepository.save(existingPlan);
    }
}