package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByActiveTrue();

    Optional<Plan> findByGoogleProductId(String googleProductId);

    Optional<Plan> findByRazorpayPlanId(String razorpayPlanId);
    
}
