package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.ai.dto.GoogleSubscriptionResponse;
import com.hinduprayerlock.backend.ai.dto.SubscriptionRequest;
import com.hinduprayerlock.backend.model.SubscriptionProvider;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.SubscriptionData;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.service.GoogleVerificationService;
import com.hinduprayerlock.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/subscription")
@RequiredArgsConstructor
public class AppSubscriptionController {

    private final SubscriptionService subscriptionService;
    private final GoogleVerificationService googleService;
    private final UserRepository userRepository;

    @PostMapping("/verify-google")
    public ResponseEntity<?> verifyGoogle(
            @RequestBody SubscriptionRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify with Google
        GoogleSubscriptionResponse googleResponse =
                googleService.verify(
                        request.getProductId(),
                        request.getPurchaseToken()
                );

        // Convert to common DTO
        SubscriptionData data = new SubscriptionData();
        data.setProvider(SubscriptionProvider.GOOGLE);
        data.setProductId(request.getProductId());
        data.setTransactionId(request.getPurchaseToken());
        data.setStartTime(googleResponse.getStartTime());
        data.setExpiryTime(googleResponse.getExpiryTime());
        data.setAutoRenewing(googleResponse.getAutoRenewing());

        subscriptionService.saveSubscription(user.getId(), data);

        return ResponseEntity.ok("Google Subscription Verified");
    }
}
