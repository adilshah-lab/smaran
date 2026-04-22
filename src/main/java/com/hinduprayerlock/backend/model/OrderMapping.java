package com.hinduprayerlock.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;   // ✅ FIXED
import jakarta.persistence.Column;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class OrderMapping {

    @Id
    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    private UUID userId;

    private Long planId;

    private Integer amount;

    private Boolean processed = false;
}