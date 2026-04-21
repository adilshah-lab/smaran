package com.hinduprayerlock.backend.model;

import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@Entity
public class OrderMapping {

    @Id
    private String orderId;

    private UUID userId;

    private Long planId;

    private Integer amount;

    private Boolean processed = false;
}
