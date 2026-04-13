package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.DailyUserStats;
import com.hinduprayerlock.backend.model.JaapStats;
import com.hinduprayerlock.backend.repository.DailyUserStatsRepository;
import com.hinduprayerlock.backend.repository.JaapStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class UserActivityService {

    private final DailyUserStatsRepository dailyRepo;
    private final JaapStatsRepository jaapRepo;

    public UserActivityService(DailyUserStatsRepository dailyRepo,
                               JaapStatsRepository jaapRepo) {
        this.dailyRepo = dailyRepo;
        this.jaapRepo = jaapRepo;
    }

    private DailyUserStats getOrCreate(UUID userId, LocalDate date) {
        return dailyRepo.findByUserIdAndDate(userId, date)
                .orElseGet(() -> {
                    DailyUserStats s = new DailyUserStats();
                    s.setUserId(userId);
                    s.setDate(date);
                    s.setJaapCount(0);
                    s.setMeditationMinutes(0);
                    s.setAppUsageMinutes(0);
                    s.setAppOpened(false);
                    return s;
                });
    }

    public void markAppOpened(UUID userId) {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        DailyUserStats stats = getOrCreate(userId, today);
        stats.setAppOpened(true);

        dailyRepo.save(stats);

        // 🔥 STREAK LOGIC (only here, no change in sync)
        JaapStats jaapStats = jaapRepo.findByUserId(userId)
                .orElseGet(() -> {
                    JaapStats j = new JaapStats();
                    j.setUserId(userId); // 🔥 IMPORTANT
                    return j;
                });

        if (jaapStats.getLastActiveDate() != null) {

            if (jaapStats.getLastActiveDate().equals(today.minusDays(1))) {
                jaapStats.setStreakDays(jaapStats.getStreakDays() + 1);
            } else if (!jaapStats.getLastActiveDate().equals(today)) {
                jaapStats.setStreakDays(1);
            }

        } else {
            jaapStats.setStreakDays(1);
        }

        jaapStats.setLastActiveDate(today);

        jaapRepo.save(jaapStats);
    }

    public void addJaap(UUID userId, int count) {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        DailyUserStats stats = getOrCreate(userId, today);

        stats.setJaapCount(stats.getJaapCount() + count);

        dailyRepo.save(stats);
    }

    public void addMeditation(UUID userId, int minutes) {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        DailyUserStats stats = getOrCreate(userId, today);

        stats.setMeditationMinutes(
                stats.getMeditationMinutes() + minutes
        );

        dailyRepo.save(stats);
    }

    public void addUsage(UUID userId, int minutes) {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        DailyUserStats stats = getOrCreate(userId, today);

        stats.setAppUsageMinutes(
                stats.getAppUsageMinutes() + minutes
        );

        dailyRepo.save(stats);
    }

    public List<DailyUserStats> getWeekly(UUID userId) {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        return dailyRepo.findByUserIdAndDateBetween(
                userId,
                today.minusDays(6),
                today
        );
    }
}
