package com.hinduprayerlock.backend.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GoogleSubscriptionResponse {

    private String orderId;

    private LocalDateTime startTime;

    private LocalDateTime expiryTime;

    private Boolean autoRenewing;
}
