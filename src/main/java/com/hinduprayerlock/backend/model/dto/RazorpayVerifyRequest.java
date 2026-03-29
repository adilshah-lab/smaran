package com.hinduprayerlock.backend.model.dto;

import lombok.Data;

@Data
public class RazorpayVerifyRequest {

    private String orderId;
    private String paymentId;
    private String signature;
}
