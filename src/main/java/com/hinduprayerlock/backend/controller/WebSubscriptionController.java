package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.OrderMapping;
import com.hinduprayerlock.backend.model.Plan;
import com.hinduprayerlock.backend.model.SubscriptionProvider;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.RazorpayVerifyRequest;
import com.hinduprayerlock.backend.model.dto.SubscriptionData;
import com.hinduprayerlock.backend.repository.OrderMappingRepository;
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
    private final OrderMappingRepository orderMappingRepository;

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

        int amount = (int) (plan.getPrice() * 100);

        JSONObject order = razorpayService.createOrder(
                amount,
                "receipt_" + System.currentTimeMillis(),
                user.getId().toString()
        );

        String orderId = order.getString("id");

        // ✅ SAVE MAPPING
        OrderMapping mapping = new OrderMapping();
        mapping.setOrderId(orderId);
        mapping.setUserId(user.getId());
        mapping.setPlanId(plan.getId());
        mapping.setAmount(amount);

        orderMappingRepository.save(mapping);

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

        boolean isValid = razorpayService.verifyPaymentSignature(
                request.getOrderId(),
                request.getPaymentId(),
                request.getSignature()
        );

        if (!isValid) {
            throw new RuntimeException("Invalid payment");
        }

        OrderMapping mapping = orderMappingRepository
                .findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔒 Ownership check
        if (!mapping.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized order");
        }

        // 🔁 Webhook fallback
        if (mapping.getProcessed()) {
            return ResponseEntity.ok("Already processed via webhook");
        }

        // 🔒 Fetch payment from Razorpay
        var payment = razorpayService.fetchPayment(request.getPaymentId());

        if (!"captured".equals(payment.get("status"))) {
            throw new RuntimeException("Payment not captured");
        }

        int paidAmount = payment.get("amount");

        if (!mapping.getAmount().equals(paidAmount)) {
            throw new RuntimeException("Amount mismatch");
        }

        Plan plan = planService.getPlanById(mapping.getPlanId());

        // 🔒 Prevent race condition
        synchronized (this) {
            if (mapping.getProcessed()) {
                return ResponseEntity.ok("Already processed");
            }

            mapping.setProcessed(true);
            orderMappingRepository.save(mapping);
        }

        SubscriptionData data = new SubscriptionData();
        data.setProvider(SubscriptionProvider.RAZORPAY);
        data.setPlanId(plan.getId());
        data.setProductId(plan.getName());
        data.setTransactionId(request.getPaymentId());
        data.setStartTime(LocalDateTime.now());
        data.setExpiryTime(LocalDateTime.now().plusDays(plan.getDurationInDays()));
        data.setAutoRenewing(false);

        subscriptionService.saveSubscription(user.getId(), data);

        return ResponseEntity.ok("Payment Verified");
    }
}