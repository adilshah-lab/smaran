package com.hinduprayerlock.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.hinduprayerlock.backend.ai.dto.GoogleSubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleVerificationService {

    @Value("${GOOGLE_SERVICE_ACCOUNT_PATH}")
    private String serviceAccountPath;

    @Value("${APP_PACKAGE_NAME}")
    private String packageName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GoogleSubscriptionResponse verify(String productId,
                                             String purchaseToken) {

        try {

            // 1️⃣ Get Access Token
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(serviceAccountPath))
                    .createScoped(Collections.singleton(
                            "https://www.googleapis.com/auth/androidpublisher"
                    ));

            credentials.refreshIfExpired();
            String accessToken = credentials.getAccessToken().getTokenValue();

            // 2️⃣ Call Google Play REST API
            String url = String.format(
                    "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/%s/purchases/subscriptions/%s/tokens/%s",
                    packageName,
                    productId,
                    purchaseToken
            );

            String response = WebClient.builder()
                    .build()
                    .get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode json = objectMapper.readTree(response);

            long expiryMillis = json.get("expiryTimeMillis").asLong();
            long startMillis = json.get("startTimeMillis").asLong();
            boolean autoRenewing = json.get("autoRenewing").asBoolean();

            return GoogleSubscriptionResponse.builder()
                    .orderId(json.get("orderId").asText())
                    .startTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(startMillis),
                            ZoneId.systemDefault()))
                    .expiryTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(expiryMillis),
                            ZoneId.systemDefault()))
                    .autoRenewing(autoRenewing)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Google subscription verification failed", e);
        }
    }
}