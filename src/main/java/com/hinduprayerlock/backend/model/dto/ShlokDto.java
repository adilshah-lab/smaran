package com.hinduprayerlock.backend.model.dto;

import lombok.Data;

@Data
public class ShlokDto {

    private Long id;
    private String sanskrit;
    private String englishTranslation;
    private String hindiTranslation;
    private String source;
}
