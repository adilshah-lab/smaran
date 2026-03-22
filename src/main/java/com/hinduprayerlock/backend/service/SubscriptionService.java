package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.dto.GoogleSubscriptionResponse;
import com.hinduprayerlock.backend.ai.dto.SubscriptionRequest;
import com.hinduprayerlock.backend.model.Subscription;
import com.hinduprayerlock.backend.model.SubscriptionStatus;
import com.hinduprayerlock.backend.repository.SubscriptionRepository;
import com.hinduprayerlock.backend.repository.UserRepository;
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
    private final UserRepository userRepository;

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

        // Create subscription entity
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setProductId(request.getProductId());
        subscription.setPurchaseToken(request.getPurchaseToken());
        subscription.setOrderId(googleResponse.getOrderId());
        subscription.setStartTime(googleResponse.getStartTime());
        subscription.setExpiryTime(googleResponse.getExpiryTime());
        subscription.setAutoRenewing(googleResponse.getAutoRenewing());
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        // Save subscription
        subscriptionRepository.save(subscription);

        // Update user subscription flag
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsSubscribed(true);
            userRepository.save(user);
        });
    }

    /**
     * Check if user has active premium
     */
    public boolean isPremium(UUID userId) {

        return subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .map(sub -> {

                    boolean active =
                            sub.getExpiryTime().isAfter(LocalDateTime.now());

                    // If expired update user flag
                    if (!active) {

                        sub.setStatus(SubscriptionStatus.EXPIRED);
                        subscriptionRepository.save(sub);

                        userRepository.findById(userId).ifPresent(user -> {
                            user.setIsSubscribed(false);
                            userRepository.save(user);
                        });
                    }

                    return active;

                }).orElse(false);
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

        // Update user subscription flag accordingly
        userRepository.findById(sub.getUserId()).ifPresent(user -> {

            if (status == SubscriptionStatus.ACTIVE) {
                user.setIsSubscribed(true);
            } else {
                user.setIsSubscribed(false);
            }

            userRepository.save(user);
        });
    }
}