package com.hinduprayerlock.backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NextVerseResponse {

    private int chapterNumber;
    private int verseNumber;
    private String verseLabel;
    private String sanskrit;
    private String hindi;
    private String english;
    private boolean isNewChapter;
}
