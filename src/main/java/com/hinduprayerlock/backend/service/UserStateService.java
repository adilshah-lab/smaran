package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.JaapStats;
import com.hinduprayerlock.backend.model.dto.UserStateResponse;
import com.hinduprayerlock.backend.repository.JaapStatsRepository;
import com.hinduprayerlock.backend.repository.LikedShlokRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStateService {

    private final LikedShlokRepository likedShlokRepository;
    private final JaapStatsRepository jaapStatsRepository;

    public UserStateService(
            LikedShlokRepository likedShlokRepository,
            JaapStatsRepository jaapStatsRepository
    ) {
        this.likedShlokRepository = likedShlokRepository;
        this.jaapStatsRepository = jaapStatsRepository;
    }

    public UserStateResponse getUserState(Long userId) {

        List<Integer> likedIds =
                likedShlokRepository.findShlokIdsByUserId(userId);

        JaapStats stats =
                jaapStatsRepository.findByUserId(userId)
                        .orElse(new JaapStats());

        UserStateResponse response = new UserStateResponse();

        response.setLikedShloks(likedIds);
        response.setTotalJaap(stats.getTotalJaap());
        response.setTodayJaap(stats.getTodayJaap());

        return response;
    }
}