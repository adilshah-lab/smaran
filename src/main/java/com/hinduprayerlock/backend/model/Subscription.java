package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    private String productId;

    @Column(columnDefinition = "TEXT")
    private String purchaseToken;

    private String orderId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private LocalDateTime startTime;

    private LocalDateTime expiryTime;

    private Boolean autoRenewing;
}