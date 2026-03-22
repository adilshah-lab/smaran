package com.hinduprayerlock.backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerseResponse {

    private int verseNumber;
    private String sanskrit;
    private boolean isRead;
}
