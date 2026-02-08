package com.hinduprayerlock.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {

    private final String userId;
    private final String email;
    private final String role;
}
