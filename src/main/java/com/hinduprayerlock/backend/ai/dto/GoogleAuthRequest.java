package com.hinduprayerlock.backend.ai.dto;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String idToken;
    private String accessToken;
    private String flow;
}
