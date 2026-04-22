package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.Subscription;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.repository.SubscriptionRepository;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.utils.InvoiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public byte[] generateInvoice(Long subscriptionId, String email) {

        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        UserEntity user = userRepository.findById(sub.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔒 SECURITY
        if (!user.getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        // 🔥 Generate invoice number once
        if (sub.getInvoiceNumber() == null) {
            sub.setInvoiceNumber(InvoiceUtil.generateInvoiceNumber());
            subscriptionRepository.save(sub);
        }

        try {
            String html = new String(
                    getClass().getClassLoader()
                            .getResourceAsStream("templates/invoice.html")
                            .readAllBytes()
            );

            // 🔥 DATE FORMAT
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

            String startDate = sub.getStartTime() != null ? sub.getStartTime().format(formatter) : "-";
            String expiryDate = sub.getExpiryTime() != null ? sub.getExpiryTime().format(formatter) : "-";

            // 🔥 TRANSACTION ID (Razorpay + Google)
            String transactionId = getTransactionId(sub);

            // 🔥 BASIC CALCULATIONS (can upgrade later)
            double amount = sub.getAmount() != null ? sub.getAmount() : 0;
            double discount = 0;
            double gst = 0;
            double total = amount;

            html = html
                    // ===== HEADER =====
                    .replace("{{invoiceNumber}}", safe(sub.getInvoiceNumber()))
                    .replace("{{plan}}", safe(sub.getPlanName()))
                    .replace("{{issueDate}}", startDate)
                    .replace("{{startDate}}", startDate)
                    .replace("{{expiryDate}}", expiryDate)

                    // ===== USER =====
                    .replace("{{name}}", safe(user.getName()))
                    .replace("{{email}}", safe(user.getEmail()))
                    .replace("{{address}}", "India") // update if available

                    // ===== SUBSCRIPTION =====
                    .replace("{{platform}}", sub.getProvider() != null ? sub.getProvider().name() : "-")
                    .replace("{{userId}}", user.getId().toString())

                    // ===== ITEMS =====
                    .replace("{{planDisplayName}}", safe(sub.getPlanName()))
                    .replace("{{duration}}", sub.getDurationDays() != null ? sub.getDurationDays() + " Days" : "-")
                    .replace("{{amount}}", String.valueOf(amount))

                    // ===== DISCOUNT =====
                    .replace("{{discountPercent}}", "0")
                    .replace("{{discountAmount}}", "0")

                    // ===== TOTAL =====
                    .replace("{{subtotal}}", String.valueOf(amount))
                    .replace("{{gst}}", String.valueOf(gst))
                    .replace("{{total}}", String.valueOf(total))

                    // ===== PAYMENT =====
                    .replace("{{paymentMethod}}", sub.getProvider() != null ? sub.getProvider().name() : "-")
                    .replace("{{transactionId}}", safe(transactionId))
                    .replace("{{paymentDate}}", startDate);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Invoice generation failed", e);
        }
    }

    // 🔥 Safe null handling
    private String safe(String value) {
        return value == null ? "-" : value;
    }

    // 🔥 Handle Razorpay + Google
    private String getTransactionId(Subscription sub) {
        if (sub.getProvider() != null && sub.getProvider().name().equals("RAZORPAY")) {
            return sub.getRazorpayPaymentId();
        } else {
            return sub.getPurchaseToken();
        }
    }
}