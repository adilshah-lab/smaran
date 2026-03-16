package com.hinduprayerlock.backend.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserSyncRequest {

    private Long userId;

    private List<Integer> likedShloks;

    private int totalJaap;

    private int todayJaap;

    private int streakDays;

}
