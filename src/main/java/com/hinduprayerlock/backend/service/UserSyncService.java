package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.JaapStats;
import com.hinduprayerlock.backend.model.LikedShlok;
import com.hinduprayerlock.backend.model.Sholak;
import com.hinduprayerlock.backend.model.dto.ShlokDto;
import com.hinduprayerlock.backend.model.dto.UserStateResponse;
import com.hinduprayerlock.backend.model.dto.UserSyncRequest;
import com.hinduprayerlock.backend.repository.JaapStatsRepository;
import com.hinduprayerlock.backend.repository.LikedShlokRepository;
import com.hinduprayerlock.backend.repository.SholakRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserSyncService {

    private final LikedShlokRepository likedRepo;
    private final JaapStatsRepository jaapRepo;
    private final SholakRepository sholakRepo;

    public UserSyncService(
            LikedShlokRepository likedRepo,
            JaapStatsRepository jaapRepo,
            SholakRepository sholakRepo
    ) {
        this.likedRepo = likedRepo;
        this.jaapRepo = jaapRepo;
        this.sholakRepo = sholakRepo;
    }

    // 🔥 SYNC API
    public void syncUserData(UUID userId, UserSyncRequest request) {

        // Save liked shloks
        for (Integer shlokId : request.getLikedShloks()) {

            if (!likedRepo.existsByUserIdAndShlokId(userId, shlokId)) {

                LikedShlok like = new LikedShlok();
                like.setUserId(userId);
                like.setShlokId(shlokId);

                likedRepo.save(like);
            }
        }

        // Save jaap stats
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

    // 🔥 STATE API (FIXED)
    public UserStateResponse getUserState(UUID userId) {

        // 1️⃣ Get liked shlok IDs
        List<LikedShlok> likedList = likedRepo.findByUserId(userId);

        List<Long> shlokIds = likedList.stream()
                .map(like -> Long.valueOf(like.getShlokId()))
                .toList();

        // 2️⃣ Fetch full shlok data (single query 🚀)
        List<Sholak> sholaks = shlokIds.isEmpty()
                ? List.of()
                : sholakRepo.findAllById(shlokIds);

        // 3️⃣ Convert to DTO
        List<ShlokDto> likedShloks = sholaks.stream()
                .map(shlok -> {
                    ShlokDto dto = new ShlokDto();
                    dto.setId(shlok.getId());
                    dto.setSanskrit(shlok.getSanskrit());
                    dto.setEnglishTranslation(shlok.getEnglishTranslation());
                    dto.setHindiTranslation(shlok.getHindiTranslation());
                    dto.setSource(shlok.getSource());
                    return dto;
                })
                .collect(Collectors.toList());

        // 4️⃣ Jaap stats
        JaapStats stats = jaapRepo.findByUserId(userId)
                .orElse(new JaapStats());

        // 5️⃣ Response
        UserStateResponse response = new UserStateResponse();
        response.setLikedShloks(likedShloks);
        response.setTotalJaap(stats.getTotalJaap());
        response.setTodayJaap(stats.getTodayJaap());
        response.setStreakDays(0); // optional for now

        return response;
    }
}