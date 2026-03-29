package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.dto.GoogleSubscriptionResponse;
import com.hinduprayerlock.backend.ai.dto.SubscriptionRequest;
import com.hinduprayerlock.backend.model.*;
import com.hinduprayerlock.backend.model.dto.SubscriptionData;
import com.hinduprayerlock.backend.repository.PlanRepository;
import com.hinduprayerlock.backend.repository.SubscriptionRepository;
import com.hinduprayerlock.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final GoogleVerificationService googleService;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    // ================= GOOGLE =================

    public void verifyAndSave(UUID userId, SubscriptionRequest request) {

        // Duplicate protection
        if (subscriptionRepository
                .findByPurchaseToken(request.getPurchaseToken())
                .isPresent()) {
            throw new RuntimeException("Purchase token already used");
        }

        // Verify with Google
        GoogleSubscriptionResponse googleResponse =
                googleService.verify(
                        request.getProductId(),
                        request.getPurchaseToken()
                );

        // Map product → plan
        Plan plan = planRepository.findByGoogleProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Plan not mapped"));

        boolean isActive = googleResponse.getExpiryTime().isAfter(LocalDateTime.now());

        SubscriptionStatus status = isActive
                ? SubscriptionStatus.ACTIVE
                : SubscriptionStatus.EXPIRED;

        Optional<Subscription> existingSub = getActiveOrCancelledSubscription(userId);

        if (existingSub.isPresent()) {

            Subscription sub = existingSub.get();

            sub.setExpiryTime(googleResponse.getExpiryTime());
            sub.setOrderId(googleResponse.getOrderId());
            sub.setPurchaseToken(request.getPurchaseToken());
            sub.setAutoRenewing(googleResponse.getAutoRenewing());
            sub.setStatus(status);
            sub.setProvider(SubscriptionProvider.GOOGLE);

            sub.setPlan(plan);
            sub.setAmount(plan.getPrice());

            // snapshot
            sub.setPlanName(plan.getName());
            sub.setDurationDays(plan.getDurationInDays());

            subscriptionRepository.save(sub);

        } else {

            Subscription sub = new Subscription();

            sub.setUserId(userId);
            sub.setProductId(plan.getName());
            sub.setPurchaseToken(request.getPurchaseToken());
            sub.setOrderId(googleResponse.getOrderId());
            sub.setProvider(SubscriptionProvider.GOOGLE);

            sub.setPlan(plan);
            sub.setAmount(plan.getPrice());

            // snapshot
            sub.setPlanName(plan.getName());
            sub.setDurationDays(plan.getDurationInDays());

            sub.setStartTime(googleResponse.getStartTime());
            sub.setExpiryTime(googleResponse.getExpiryTime());
            sub.setAutoRenewing(googleResponse.getAutoRenewing());
            sub.setStatus(status);
            sub.setCreatedAt(LocalDateTime.now());

            subscriptionRepository.save(sub);
        }

        updateUserSubscriptionFlag(userId, isActive);
    }

    // ================= RAZORPAY =================

    public void saveSubscription(UUID userId, SubscriptionData data) {

        // Duplicate protection
        if (data.getProvider() == SubscriptionProvider.RAZORPAY) {
            if (subscriptionRepository
                    .findByRazorpayPaymentId(data.getTransactionId())
                    .isPresent()) {
                return;
            }
        }

        Plan plan = planRepository.findById(data.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Optional<Subscription> existingSub = getActiveOrCancelledSubscription(userId);

        if (existingSub.isPresent()) {

            Subscription sub = existingSub.get();

            LocalDateTime newExpiry = sub.getExpiryTime()
                    .plusDays(plan.getDurationInDays());

            sub.setExpiryTime(newExpiry);
            sub.setPlan(plan);
            sub.setAmount(plan.getPrice());

            // snapshot
            sub.setPlanName(plan.getName());
            sub.setDurationDays(plan.getDurationInDays());

            subscriptionRepository.save(sub);

        } else {

            Subscription sub = new Subscription();

            sub.setUserId(userId);
            sub.setProductId(plan.getName());
            sub.setProvider(data.getProvider());
            sub.setPlan(plan);

            if (data.getProvider() == SubscriptionProvider.RAZORPAY) {
                sub.setRazorpayPaymentId(data.getTransactionId());
            }

            sub.setAmount(plan.getPrice());

            // snapshot
            sub.setPlanName(plan.getName());
            sub.setDurationDays(plan.getDurationInDays());

            sub.setStartTime(LocalDateTime.now());
            sub.setExpiryTime(LocalDateTime.now().plusDays(plan.getDurationInDays()));
            sub.setAutoRenewing(data.isAutoRenewing());
            sub.setStatus(SubscriptionStatus.ACTIVE);
            sub.setCreatedAt(LocalDateTime.now());

            subscriptionRepository.save(sub);
        }

        updateUserSubscriptionFlag(userId, true);
    }

    // ================= COMMON =================

    public boolean isPremium(UUID userId) {
        return getActiveOrCancelledSubscription(userId).isPresent();
    }

    public Subscription getUserSubscription(UUID userId) {
        return getActiveOrCancelledSubscription(userId).orElse(null);
    }

    public void cancelSubscription(UUID userId) {

        Subscription sub = getActiveOrCancelledSubscription(userId)
                .orElseThrow(() -> new RuntimeException("No active subscription"));

        sub.setStatus(SubscriptionStatus.CANCELLED);
        sub.setAutoRenewing(false);

        subscriptionRepository.save(sub);

        updateUserSubscriptionFlag(userId, true); // still valid till expiry
    }

    // ================= ADMIN =================

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public void updateStatus(Long id, SubscriptionStatus status) {

        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        sub.setStatus(status);
        subscriptionRepository.save(sub);

        updateUserSubscriptionFlag(sub.getUserId(),
                status == SubscriptionStatus.ACTIVE
                        || status == SubscriptionStatus.CANCELLED);
    }

    public double calculateRevenue() {
        return subscriptionRepository.findAll().stream()
                .filter(this::isValidSubscription)
                .mapToDouble(s -> s.getAmount() != null ? s.getAmount() : 0)
                .sum();
    }

    public long getActiveUserCount() {
        return subscriptionRepository.findAll().stream()
                .filter(this::isValidSubscription)
                .count();
    }

    public Map<String, Long> getPlanStats() {
        return subscriptionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        s -> s.getPlan().getName(),
                        Collectors.counting()
                ));
    }

    public List<Subscription> getExpiringSubscriptions() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next3Days = now.plusDays(3);

        return subscriptionRepository.findAll().stream()
                .filter(sub ->
                        sub.getExpiryTime().isAfter(now)
                                && sub.getExpiryTime().isBefore(next3Days)
                )
                .toList();
    }

    public List<Subscription> getByStatus(SubscriptionStatus status) {
        return subscriptionRepository.findAll().stream()
                .filter(sub -> sub.getStatus() == status)
                .toList();
    }

    // ================= HELPERS =================

    private Optional<Subscription> getActiveOrCancelledSubscription(UUID userId) {

        return subscriptionRepository.findByUserId(userId)
                .stream()
                .filter(this::isValidSubscription)
                .sorted(Comparator.comparing(Subscription::getExpiryTime).reversed())
                .findFirst();
    }

    private boolean isValidSubscription(Subscription sub) {
        return (sub.getStatus() == SubscriptionStatus.ACTIVE
                || sub.getStatus() == SubscriptionStatus.CANCELLED)
                && sub.getExpiryTime().isAfter(LocalDateTime.now());
    }

    // 🔥 IMPORTANT: make it public (used by scheduler)
    public void updateUserSubscriptionFlag(UUID userId, boolean isSubscribed) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsSubscribed(isSubscribed);
            userRepository.save(user);
        });
    }
}