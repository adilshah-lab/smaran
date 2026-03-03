package com.hinduprayerlock.backend.Scheduler;

import com.hinduprayerlock.backend.model.Subscription;
import com.hinduprayerlock.backend.model.SubscriptionStatus;
import com.hinduprayerlock.backend.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionExpiryScheduler {

    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    public void expireSubscriptions() {

        List<Subscription> activeSubs =
                subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);

        for (Subscription sub : activeSubs) {

            if (sub.getExpiryTime()
                    .isBefore(LocalDateTime.now())) {

                sub.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(sub);
            }
        }
    }
}
