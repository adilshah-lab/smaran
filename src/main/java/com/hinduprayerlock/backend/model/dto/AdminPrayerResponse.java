package com.hinduprayerlock.backend.model.dto;

import com.hinduprayerlock.backend.model.Mood;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminPrayerResponse(
        UUID id,
        Mood mood,
        String sanskrit,
        String english,
        String hindi,
        boolean active,
        LocalDateTime createdAt
) {}
