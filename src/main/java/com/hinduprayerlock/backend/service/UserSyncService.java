package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.JaapStats;
import com.hinduprayerlock.backend.model.LikedShlok;
import com.hinduprayerlock.backend.model.dto.UserStateResponse;
import com.hinduprayerlock.backend.model.dto.UserSyncRequest;
import com.hinduprayerlock.backend.repository.JaapStatsRepository;
import com.hinduprayerlock.backend.repository.LikedShlokRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserSyncService {

    private final LikedShlokRepository likedRepo;
    private final JaapStatsRepository jaapRepo;

    public UserSyncService(
            LikedShlokRepository likedRepo,
            JaapStatsRepository jaapRepo) {

        this.likedRepo = likedRepo;
        this.jaapRepo = jaapRepo;
    }

    public void syncUserData(UserSyncRequest request) {

        Long userId = request.getUserId();

        // Save Jaap stats
        JaapStats stats = jaapRepo.findByUserId(userId)
                .orElse(new JaapStats());

        stats.setUserId(userId);
        stats.setTotalJaap(request.getTotalJaap());
        stats.setTodayJaap(request.getTodayJaap());

        jaapRepo.save(stats);

        // Save liked shloks
        likedRepo.deleteByUserId(userId);

        request.getLikedShloks().forEach(shlokId -> {

            LikedShlok like = new LikedShlok();
            like.setId(UUID.randomUUID());
            like.setUserId(userId);
            like.setShlokId(shlokId);

            likedRepo.save(like);
        });
    }

    public UserStateResponse getUserState(Long userId){

        UserStateResponse response = new UserStateResponse();

        response.setLikedShloks(
                likedRepo.findByUserId(userId)
                        .stream()
                        .map(LikedShlok::getShlokId)
                        .toList()
        );

        JaapStats stats = jaapRepo.findByUserId(userId)
                .orElse(new JaapStats());

        response.setTotalJaap(stats.getTotalJaap());
        response.setTodayJaap(stats.getTodayJaap());

        return response;
    }
}
