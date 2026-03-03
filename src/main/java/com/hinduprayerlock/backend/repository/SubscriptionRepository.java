package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.Subscription;
import com.hinduprayerlock.backend.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserIdAndStatus(UUID userId, SubscriptionStatus status);

    Optional<Subscription> findByPurchaseToken(String purchaseToken);

    List<Subscription> findByStatus(SubscriptionStatus status);
}
