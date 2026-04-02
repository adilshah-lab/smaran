package com.hinduprayerlock.backend.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GoogleTokenVerifier {

    private static final Logger log = LoggerFactory.getLogger(GoogleTokenVerifier.class);

    @Value("${GOOGLE_CLIENT_ID}")
    private String clientIds;

    public GoogleIdToken.Payload verify(String idTokenString) {

        try {
            List<String> audiences = Arrays.asList(clientIds.split(","));

            log.info("🔍 Verifying Google token...");
            log.info("🎯 Allowed audiences: {}", audiences);
            log.info("🪙 Token (first 20 chars): {}", idTokenString.substring(0, Math.min(20, idTokenString.length())));

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            )
                    .setAudience(audiences)
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                log.error("❌ Token verification returned null — audience mismatch or expired token");
                log.error("❌ Token audience in JWT may not match: {}", audiences);
                throw new RuntimeException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            log.info("✅ Token verified successfully");
            log.info("📧 Email: {}", payload.getEmail());
            log.info("👤 Name: {}", payload.get("name"));
            log.info("🎯 Audience in token: {}", payload.getAudience());

            return payload;

        } catch (RuntimeException e) {
            throw e; // rethrow already handled
        } catch (Exception e) {
            log.error("❌ Google token verification failed: {}", e.getMessage(), e);
            throw new RuntimeException("Google token verification failed: " + e.getMessage());
        }
    }
}