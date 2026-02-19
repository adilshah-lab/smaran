package com.hinduprayerlock.backend.model.dto;


public record SholakResponse(
        Long id,
//        String title,
        String sanskrit,
        String englishTranslation,
        String hindiTranslation,
        String source
) {}
