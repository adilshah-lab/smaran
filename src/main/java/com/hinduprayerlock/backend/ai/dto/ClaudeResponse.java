package com.hinduprayerlock.backend.ai.dto;

import java.util.List;
import java.util.Map;

public class ClaudeResponse {

    public List<Map<String, Object>> content;

    public String getText() {
        if (content == null || content.isEmpty()) return null;
        return (String) content.get(0).get("text");
    }
}
