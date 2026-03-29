package com.hinduprayerlock.backend.Scheduler;

import com.hinduprayerlock.backend.model.Subscription;
import com.hinduprayerlock.backend.model.SubscriptionStatus;
import com.hinduprayerlock.backend.repository.SubscriptionRepository;
import com.hinduprayerlock.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionExpiryScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void expireSubscriptions() {

        List<Subscription> subs = subscriptionRepository.findAll();

        for (Subscription sub : subs) {

            if (sub.getExpiryTime().isBefore(LocalDateTime.now())
                    && sub.getStatus() != SubscriptionStatus.EXPIRED) {

                sub.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(sub);

                // 🔥 IMPORTANT
                subscriptionService.updateUserSubscriptionFlag(
                        sub.getUserId(),
                        false
                );
            }
        }
    }
}
