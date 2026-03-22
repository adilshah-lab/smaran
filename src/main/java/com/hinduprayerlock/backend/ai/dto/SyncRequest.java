package com.hinduprayerlock.backend.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class SyncRequest {

    private List<Integer> likedShloks;

    private Integer totalJaap;

    private Integer todayJaap;
}
