package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class CommonSubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(Authentication authentication) {

        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isPremium = subscriptionService.isPremium(user.getId());

        return ResponseEntity.ok(isPremium);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMySubscription(Authentication authentication) {

        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                subscriptionService.getUserSubscription(user.getId())
        );
    }
}
