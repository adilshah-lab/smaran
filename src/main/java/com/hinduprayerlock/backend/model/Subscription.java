package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_expiry", columnList = "expiry_time")
})
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    private String productId;

    // Google fields
    @Column(columnDefinition = "TEXT", unique = true)
    private String purchaseToken;

    private String orderId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    private SubscriptionProvider provider;

    // Razorpay fields
    @Column(unique = true)
    private String razorpayPaymentId;

    private String razorpaySubscriptionId;

    // Plan relation
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private Double amount;

    // Snapshot
    private String planName;
    private Integer durationDays;

    private LocalDateTime startTime;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    private Boolean autoRenewing;

    @Column(unique = true)
    private String invoiceNumber;

    // Optional relation (READ ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
