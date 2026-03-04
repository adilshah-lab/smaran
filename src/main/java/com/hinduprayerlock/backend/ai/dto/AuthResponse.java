package com.hinduprayerlock.backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private LocalDateTime createdAt;

    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
