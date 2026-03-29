package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.Subscription;
import com.hinduprayerlock.backend.model.SubscriptionProvider;
import com.hinduprayerlock.backend.model.SubscriptionStatus;
import com.hinduprayerlock.backend.model.dto.SubscriptionData;
import com.hinduprayerlock.backend.repository.SubscriptionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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

    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;

    /**
     * ✅ Create Order
     */
    public JSONObject createOrder(int amount, String receiptId, String userId) {

        try {
            RazorpayClient client = new RazorpayClient(key, secret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);

            JSONObject notes = new JSONObject();
            notes.put("userId", userId);

            orderRequest.put("notes", notes);

            Order order = client.orders.create(orderRequest);

            return order.toJson();

        } catch (Exception e) {
            throw new RuntimeException("Error creating order", e);
        }
    }

    /**
     * ✅ Verify Payment Signature
     */
    public boolean verifyPaymentSignature(
            String orderId,
            String paymentId,
            String razorpaySignature
    ) {

        try {
            String payload = orderId + "|" + paymentId;
            String generatedSignature = hmacSha256(payload, secret);

            return generatedSignature.equals(razorpaySignature);

        } catch (Exception e) {
            throw new RuntimeException("Error verifying payment signature", e);
        }
    }

    /**
     * ✅ Verify Webhook Signature
     */
    public boolean verifyWebhookSignature(String payload, String actualSignature) {

        try {
            String expectedSignature = hmacSha256(payload, webhookSecret);
            return expectedSignature.equals(actualSignature);

        } catch (Exception e) {
            throw new RuntimeException("Webhook signature verification failed", e);
        }
    }

    /**
     * ✅ Process Webhook Events
     */
    public void processWebhook(String payload, String signature) {

        if (!verifyWebhookSignature(payload, signature)) {
            throw new RuntimeException("Invalid webhook signature");
        }

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        System.out.println("Webhook Event: " + event);

        switch (event) {

            case "payment.captured":
                handlePaymentCaptured(json);
                break;

            case "payment.failed":
                handlePaymentFailed(json);
                break;

            case "subscription.charged":
                handleSubscriptionCharged(json);
                break;

            case "subscription.cancelled":
                handleSubscriptionCancelled(json);
                break;

            default:
                System.out.println("Unhandled webhook event: " + event);
        }
    }

    /**
     * ✅ Handle successful payment
     */
    private void handlePaymentCaptured(JSONObject json) {

        JSONObject paymentEntity =
                json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

        String paymentId = paymentEntity.getString("id");

        // 🔥 Prevent duplicate processing
        if (subscriptionRepository.findByRazorpayPaymentId(paymentId).isPresent()) {
            return;
        }

        String userId = paymentEntity
                .getJSONObject("notes")
                .getString("userId");

        System.out.println("Payment for user: " + userId);

        SubscriptionData data = new SubscriptionData();
        data.setProvider(SubscriptionProvider.RAZORPAY);
        data.setProductId("premium_plan");
        data.setTransactionId(paymentId);
        data.setStartTime(LocalDateTime.now());
        data.setExpiryTime(LocalDateTime.now().plusMonths(1));
        data.setAutoRenewing(false);

        subscriptionService.saveSubscription(UUID.fromString(userId), data);
    }

    /**
     * ❌ Handle failed payment
     */
    private void handlePaymentFailed(JSONObject json) {

        JSONObject paymentEntity =
                json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

        String paymentId = paymentEntity.getString("id");

        System.out.println("Payment Failed: " + paymentId);
    }

    /**
     * 🔁 Handle recurring subscription payment
     */
    private void handleSubscriptionCharged(JSONObject json) {

        JSONObject subEntity =
                json.getJSONObject("payload")
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

        String subscriptionId = subEntity.getString("id");

        Subscription sub = subscriptionRepository
                .findByRazorpaySubscriptionId(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // 🔥 FIX: Use plan duration (not snapshot)
        int duration = sub.getPlan().getDurationInDays();

        // 🔥 Safe extension
        LocalDateTime baseTime = sub.getExpiryTime().isAfter(LocalDateTime.now())
                ? sub.getExpiryTime()
                : LocalDateTime.now();

        sub.setExpiryTime(baseTime.plusDays(duration));
        sub.setStatus(SubscriptionStatus.ACTIVE);

        subscriptionRepository.save(sub);

        subscriptionService.updateUserSubscriptionFlag(sub.getUserId(), true);
    }

    /**
     * ❌ Handle subscription cancellation
     */
    private void handleSubscriptionCancelled(JSONObject json) {

        JSONObject subEntity =
                json.getJSONObject("payload")
                        .getJSONObject("subscription")
                        .getJSONObject("entity");

        String subscriptionId = subEntity.getString("id");

        System.out.println("Subscription Cancelled: " + subscriptionId);

        Subscription sub = subscriptionRepository
                .findByRazorpaySubscriptionId(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        sub.setStatus(SubscriptionStatus.CANCELLED);
        sub.setAutoRenewing(false);

        subscriptionRepository.save(sub);

        subscriptionService.updateUserSubscriptionFlag(sub.getUserId(), true);
    }

    /**
     * 🔐 HMAC SHA256 Generator
     */
    private String hmacSha256(String data, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        mac.init(secretKey);

        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hash);
    }

    /**
     * 🔁 Convert byte[] → hex
     */
    private String bytesToHex(byte[] bytes) {

        StringBuilder hex = new StringBuilder(2 * bytes.length);

        for (byte b : bytes) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1) hex.append('0');
            hex.append(s);
        }

        return hex.toString();
    }
}