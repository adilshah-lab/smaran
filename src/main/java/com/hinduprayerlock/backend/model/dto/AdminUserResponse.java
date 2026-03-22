package com.hinduprayerlock.backend.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String email,
        LocalDateTime createdAt,
        long prayersConsumed
) {}
