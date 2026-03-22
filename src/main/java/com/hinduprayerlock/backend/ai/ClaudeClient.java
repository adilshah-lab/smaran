package com.hinduprayerlock.backend.ai;

import com.hinduprayerlock.backend.ai.dto.ClaudeRequest;
import com.hinduprayerlock.backend.ai.dto.ClaudeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClaudeClient {

    @Value("${CLAUDE_API_KEY}")
    private String apiKey;

    @Value("${CLAUDE_API_URL}")
    private String apiUrl;

    @Value("${CLAUDE_MODEL}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generatePrayer(String prompt) {

        ClaudeRequest request = new ClaudeRequest(
                model,
                400,
                java.util.List.of(
                        java.util.Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        HttpEntity<ClaudeRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ClaudeResponse> response =
                restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        entity,
                        ClaudeResponse.class
                );

        return response.getBody().getText();
    }
}
