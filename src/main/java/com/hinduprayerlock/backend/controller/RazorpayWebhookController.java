package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhook/razorpay")
@RequiredArgsConstructor
public class RazorpayWebhookController {

    private final RazorpayService razorpayService;

    @PostMapping
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature
    ) {

        razorpayService.processWebhook(payload, signature);

        return ResponseEntity.ok("Webhook handled");
    }
}
