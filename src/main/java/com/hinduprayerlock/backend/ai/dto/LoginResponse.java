package com.hinduprayerlock.backend.ai.dto;

import java.time.LocalDateTime;

public class LoginResponse {

    private String token;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public LoginResponse(String token, String name, String email , LocalDateTime createdAt) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
