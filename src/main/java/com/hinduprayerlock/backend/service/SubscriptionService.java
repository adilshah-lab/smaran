package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.dto.GoogleSubscriptionResponse;
import com.hinduprayerlock.backend.ai.dto.SubscriptionRequest;
import com.hinduprayerlock.backend.model.Subscription;
import com.hinduprayerlock.backend.model.SubscriptionStatus;
import com.hinduprayerlock.backend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final GoogleVerificationService googleService;

    /**
     * Verify subscription with Google and save in DB
     */
    public void verifyAndSave(UUID userId, SubscriptionRequest request) {

        // Prevent duplicate purchase token
        if (subscriptionRepository
                .findByPurchaseToken(request.getPurchaseToken())
                .isPresent()) {

            throw new RuntimeException("Purchase token already used");
        }

        // Verify with Google Play
        GoogleSubscriptionResponse googleResponse =
                googleService.verify(
                        request.getProductId(),
                        request.getPurchaseToken()
                );

        // Save subscription
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);  // UUID here
        subscription.setProductId(request.getProductId());
        subscription.setPurchaseToken(request.getPurchaseToken());
        subscription.setOrderId(googleResponse.getOrderId());
        subscription.setStartTime(googleResponse.getStartTime());
        subscription.setExpiryTime(googleResponse.getExpiryTime());
        subscription.setAutoRenewing(googleResponse.getAutoRenewing());
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        subscriptionRepository.save(subscription);
    }

    /**
     * Check if user has active premium
     */
    public boolean isPremium(UUID userId) {

        return subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .filter(sub -> sub.getExpiryTime()
                        .isAfter(LocalDateTime.now()))
                .isPresent();
    }

    /**
     * Get active subscription for user
     */
    public Subscription getUserSubscription(UUID userId) {

        return subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);
    }

    /**
     * Admin: Get all subscriptions
     */
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    /**
     * Admin: Update subscription status manually
     */
    public void updateStatus(Long id, SubscriptionStatus status) {

        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        sub.setStatus(status);
        subscriptionRepository.save(sub);
    }
}