package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.AiSettings;
import com.hinduprayerlock.backend.repository.AiSettingsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAiService {

    private final AiSettingsRepository repo;

    public AiSettings get() {
        return repo.findById(1L).orElseGet(AiSettings::new);
    }

    @Transactional
    public AiSettings update(boolean enabled, int maxCalls) {
        AiSettings s = get();
        s.setEnabled(enabled);
        s.setMaxCallsPerDay(maxCalls);
        return repo.save(s);
    }
}
