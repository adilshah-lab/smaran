package com.hinduprayerlock.backend.ai.dto;

import java.util.List;
import java.util.Map;

public class ClaudeRequest {

    public String model;
    public int max_tokens;
    public List<Map<String, String>> messages;

    public ClaudeRequest(String model, int maxTokens, List<Map<String, String>> messages) {
        this.model = model;
        this.max_tokens = maxTokens;
        this.messages = messages;
    }
}
