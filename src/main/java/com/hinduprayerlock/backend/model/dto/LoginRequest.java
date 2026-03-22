package com.hinduprayerlock.backend.model.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String password;
    private String identifier;
}