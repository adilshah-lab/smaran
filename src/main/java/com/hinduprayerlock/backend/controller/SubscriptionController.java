package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.ai.dto.SubscriptionRequest;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestBody SubscriptionRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName(); // logged-in user's email

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        subscriptionService.verifyAndSave(
                user.getId(),   // UUID
                request
        );

        return ResponseEntity.ok("Subscription Verified");
    }

//    @GetMapping("/me")
//    public ResponseEntity<?> getMySubscription(Authentication authentication) {
//
//        String email = authentication.getName();
//
//        UserEntity user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return ResponseEntity.ok(
//                subscriptionService.getUserSubscription(user.getId())
//        );
//    }

//    @GetMapping("/status")
//    public ResponseEntity<?> checkPremium(Authentication authentication) {
//
//        String email = authentication.getName();
//
//        UserEntity user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return ResponseEntity.ok(
//                subscriptionService.isPremium(user.getId())
//        );
//    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSubscription(Authentication authentication) {

        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        subscriptionService.cancelSubscription(user.getId());

        return ResponseEntity.ok("Subscription cancelled");
    }
}