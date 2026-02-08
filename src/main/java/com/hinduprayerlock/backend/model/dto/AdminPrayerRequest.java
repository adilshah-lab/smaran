package com.hinduprayerlock.backend.model.dto;

import com.hinduprayerlock.backend.model.Mood;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminPrayerRequest(

        @NotNull
        Mood mood,

        @NotBlank
        String sanskrit,

        @NotBlank
        String english,

        @NotBlank
        String hindi
) {}
