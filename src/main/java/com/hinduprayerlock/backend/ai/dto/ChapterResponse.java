package com.hinduprayerlock.backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapterResponse {

    private int chapterNumber;
    private int totalVerses;
    private int readVerses;
    private String imageName;
}