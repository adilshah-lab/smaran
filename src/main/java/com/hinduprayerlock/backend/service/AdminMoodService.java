package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.MoodConfig;
import com.hinduprayerlock.backend.repository.MoodConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMoodService {

    private final MoodConfigRepository repo;

    public List<MoodConfig> getAll() {
        return repo.findAll();
    }

    @Transactional
    public MoodConfig update(Mood mood, boolean active, String description) {

        MoodConfig config = repo.findById(mood)
                .orElseGet(() -> {
                    MoodConfig m = new MoodConfig();
                    m.setMood(mood);
                    return m;
                });

        config.setActive(active);
        config.setDescription(description);
        return repo.save(config);
    }
}
