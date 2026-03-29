package com.hinduprayerlock.backend.model.dto;

import com.hinduprayerlock.backend.model.SubscriptionProvider;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionData {

    private SubscriptionProvider provider;

    private Long planId;

    private String productId;

    private String transactionId;

    private LocalDateTime startTime;

    private LocalDateTime expiryTime;

    private String subscriptionId;

    private boolean autoRenewing;
}
