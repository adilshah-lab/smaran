package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.Plan;
import com.hinduprayerlock.backend.model.SubscriptionProvider;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.RazorpayVerifyRequest;
import com.hinduprayerlock.backend.model.dto.SubscriptionData;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.service.PlanService;
import com.hinduprayerlock.backend.service.RazorpayService;
import com.hinduprayerlock.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/web/subscription")
@RequiredArgsConstructor
public class WebSubscriptionController {

    private final RazorpayService razorpayService;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final PlanService planService;

    // ================= CREATE ORDER =================

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestParam Long planId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Plan plan = planService.getPlanById(planId);

        int amount = (int) (plan.getPrice() * 100); // ₹ → paise

        JSONObject order = razorpayService.createOrder(
                amount,
                "receipt_" + System.currentTimeMillis(),
                user.getId().toString()
        );

        return ResponseEntity.ok(order.toString());
    }

    // ================= VERIFY PAYMENT =================

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(
            @RequestBody RazorpayVerifyRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔒 Step 1: Verify signature
        boolean isValid = razorpayService.verifyPaymentSignature(
                request.getOrderId(),
                request.getPaymentId(),
                request.getSignature()
        );

        if (!isValid) {
            throw new RuntimeException("Invalid payment");
        }

        // 🔥 Step 2: Fetch Plan (MANDATORY FIX)
        Plan plan = planService.getPlanById(request.getPlanId());

        // 🔒 Step 3: (Optional but recommended) Validate amount
        int expectedAmount = (int) (plan.getPrice() * 100);

        // 👉 If you pass amount from frontend, validate it here
        if (request.getAmount() != null && request.getAmount() != expectedAmount) {
            throw new RuntimeException("Amount mismatch");
        }

        // 🔁 Step 4: Convert to common DTO
        SubscriptionData data = new SubscriptionData();
        data.setProvider(SubscriptionProvider.RAZORPAY);
        data.setPlanId(plan.getId()); // ✅ FIXED
        data.setProductId(plan.getName());
        data.setTransactionId(request.getPaymentId());
        data.setSubscriptionId(null); // if using order-based flow
        data.setStartTime(LocalDateTime.now());
        data.setExpiryTime(LocalDateTime.now().plusDays(plan.getDurationInDays()));
        data.setAutoRenewing(false);

        // 💾 Step 5: Save subscription
        subscriptionService.saveSubscription(user.getId(), data);

        return ResponseEntity.ok("Payment Verified");
    }
}