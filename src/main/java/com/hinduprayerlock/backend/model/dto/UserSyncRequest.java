package com.hinduprayerlock.backend.model.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class UserSyncRequest {

    private UUID userId;

    private List<Integer> likedShloks;

    private int totalJaap;

    private int todayJaap;

    private int streakDays;

}
