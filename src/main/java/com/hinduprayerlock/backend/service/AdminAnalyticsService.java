package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.PrayerEntity;
import com.hinduprayerlock.backend.repository.PrayerRepository;
import com.hinduprayerlock.backend.repository.UserPrayerHistoryRepository;
import com.hinduprayerlock.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsService {

    private final UserRepository userRepo;
    private final UserPrayerHistoryRepository historyRepo;
    private final PrayerRepository prayerRepo;

    public Map<String, Object> overview() {

        long totalUsers = userRepo.count();
        long totalServed = historyRepo.count();

        return Map.of(
                "totalUsers", totalUsers,
                "prayersServed", totalServed
        );
    }

    public List<Map<String, Object>> moodStats() {

        return prayerRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        PrayerEntity::getMood,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mood", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .toList();
    }
}
