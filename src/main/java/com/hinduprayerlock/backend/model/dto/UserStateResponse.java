package com.hinduprayerlock.backend.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserStateResponse {

    private List<ShlokDto> likedShloks;

    private int totalJaap;

    private int todayJaap;

    private int streakDays;
}
