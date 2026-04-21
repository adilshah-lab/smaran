package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.*;
import com.hinduprayerlock.backend.model.Plan;
import com.hinduprayerlock.backend.model.dto.SubscriptionData;
import com.hinduprayerlock.backend.repository.OrderMappingRepository;
import com.hinduprayerlock.backend.repository.PlanRepository;
import com.hinduprayerlock.backend.repository.SubscriptionRepository;
import com.razorpay.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    @Value("${RAZORPAY_KEY}")
    private String key;

    @Value("${RAZORPAY_SECRET}")
    private String secret;

    @Value("${RAZORPAY_WEBHOOK_SECRET}")
    private String webhookSecret;

    private RazorpayClient razorpayClient; // ✅ GLOBAL CLIENT

    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final OrderMappingRepository orderMappingRepository;
    private final PlanRepository planRepository;

    // ✅ INIT CLIENT (VERY IMPORTANT)
    @PostConstruct
    public void init() {
        try {
            this.razorpayClient = new RazorpayClient(key, secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Razorpay client", e);
        }
    }

    // ================= CREATE ORDER =================
    public JSONObject createOrder(int amount, String receiptId, String userId) {

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);

            JSONObject notes = new JSONObject();
            notes.put("userId", userId);

            orderRequest.put("notes", notes);

            Order order = razorpayClient.orders.create(orderRequest);

            return order.toJson();

        } catch (Exception e) {
            throw new RuntimeException("Error creating order", e);
        }
    }

    // ================= VERIFY PAYMENT SIGNATURE =================
    public boolean verifyPaymentSignature(
            String orderId,
            String paymentId,
            String razorpaySignature
    ) {
        try {
            String payload = orderId + "|" + paymentId;
            return Utils.verifySignature(payload, razorpaySignature, secret);
        } catch (Exception e) {
            return false;
        }
    }

    // ================= VERIFY WEBHOOK SIGNATURE =================
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            return Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (Exception e) {
            return false;
        }
    }

    // ================= FETCH PAYMENT =================
    public Payment fetchPayment(String paymentId) {
        try {
            return razorpayClient.payments.fetch(paymentId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch payment from Razorpay");
        }
    }

    // ================= PROCESS WEBHOOK =================
    public void processWebhook(String payload, String signature) {

        if (!verifyWebhookSignature(payload, signature)) {
            throw new RuntimeException("Invalid webhook signature");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if (!"payment.captured".equals(eventType)) {
            return;
        }

        JSONObject paymentEntity = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String paymentId = paymentEntity.getString("id");
        String orderId = paymentEntity.getString("order_id");
        int amount = paymentEntity.getInt("amount");

        OrderMapping mapping = orderMappingRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order mapping not found"));

        if (mapping.getProcessed()) return;

        if (!mapping.getAmount().equals(amount)) {
            throw new RuntimeException("Amount mismatch in webhook");
        }

        Plan plan = planRepository.findById(mapping.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        SubscriptionData data = new SubscriptionData();
        data.setProvider(SubscriptionProvider.RAZORPAY);
        data.setPlanId(plan.getId());
        data.setProductId(plan.getName());
        data.setTransactionId(paymentId);
        data.setAutoRenewing(false);

        subscriptionService.saveSubscription(mapping.getUserId(), data);

        mapping.setProcessed(true);
        orderMappingRepository.save(mapping);
    }
}