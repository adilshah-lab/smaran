package com.hinduprayerlock.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthUser {

    private UUID id;
    private final String email;
    private final String role;
}
