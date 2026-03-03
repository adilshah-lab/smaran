package com.hinduprayerlock.backend.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubscriptionRequest {

    @NotBlank
    private String productId;

    @NotBlank
    private String purchaseToken;

    @NotBlank
    private String packageName;
}
