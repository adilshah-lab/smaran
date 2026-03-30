package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.Subscription;
import com.hinduprayerlock.backend.model.SubscriptionStatus;
import com.hinduprayerlock.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/subscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public List<Subscription> getAll() {
        return subscriptionService.getAllSubscriptions();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam SubscriptionStatus status
    ) {
        subscriptionService.updateStatus(id, status);
        return ResponseEntity.ok("Updated");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSubscription(
            @PathVariable UUID userId   // ✅ UUID
    ) {
        return ResponseEntity.ok(
                subscriptionService.getUserSubscription(userId)  // ✅ pass variable
        );
    }

    @GetMapping("/analytics/revenue")
    public ResponseEntity<?> getRevenue() {

        double total = subscriptionService.calculateRevenue();

        return ResponseEntity.ok(total);
    }

    @GetMapping("/analytics/active-users")
    public ResponseEntity<?> getActiveUsers() {

        long count = subscriptionService.getActiveUserCount();

        return ResponseEntity.ok(count);
    }

    @GetMapping("/analytics/plan-stats")
    public ResponseEntity<?> getPlanStats() {

        return ResponseEntity.ok(subscriptionService.getPlanStats());
    }

    @GetMapping("/analytics/expiring-soon")
    public ResponseEntity<?> expiringSoon() {

        return ResponseEntity.ok(subscriptionService.getExpiringSubscriptions());
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterByStatus(
            @RequestParam SubscriptionStatus status
    ) {
        return ResponseEntity.ok(
                subscriptionService.getByStatus(status)
        );
    }
}
