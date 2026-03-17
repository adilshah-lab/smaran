package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.JaapStats;
import com.hinduprayerlock.backend.model.LikedShlok;
import com.hinduprayerlock.backend.model.dto.UserStateResponse;
import com.hinduprayerlock.backend.model.dto.UserSyncRequest;
import com.hinduprayerlock.backend.repository.JaapStatsRepository;
import com.hinduprayerlock.backend.repository.LikedShlokRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserSyncService {

    private final LikedShlokRepository likedRepo;
    private final JaapStatsRepository jaapRepo;

    public UserSyncService(
            LikedShlokRepository likedRepo,
            JaapStatsRepository jaapRepo
    ) {
        this.likedRepo = likedRepo;
        this.jaapRepo = jaapRepo;
    }

    public void syncUserData(UUID userId, UserSyncRequest request) {

        for (Integer shlokId : request.getLikedShloks()) {

            if (!likedRepo.existsByUserIdAndShlokId(userId, shlokId)) {

                LikedShlok like = new LikedShlok();
                like.setUserId(userId);
                like.setShlokId(shlokId);

                likedRepo.save(like);
            }
        }

        JaapStats stats = jaapRepo.findByUserId(userId)
                .orElseGet(() -> {
                    JaapStats newStats = new JaapStats();
                    newStats.setUserId(userId);
                    return newStats;
                });

        stats.setTotalJaap(
                Math.max(stats.getTotalJaap(), request.getTotalJaap())
        );

        stats.setTodayJaap(
                Math.max(stats.getTodayJaap(), request.getTodayJaap())
        );

        jaapRepo.save(stats);
    }

    public UserStateResponse getUserState(UUID userId) {

        List<Integer> likedIds = likedRepo.findByUserId(userId)
                .stream()
                .map(LikedShlok::getShlokId)
                .collect(Collectors.toList());

        JaapStats stats = jaapRepo.findByUserId(userId)
                .orElse(new JaapStats());

        UserStateResponse response = new UserStateResponse();
        response.setLikedShloks(likedIds);
        response.setTotalJaap(stats.getTotalJaap());
        response.setTodayJaap(stats.getTodayJaap());

        return response;
    }
}