package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.PrayerEntity;
import com.hinduprayerlock.backend.model.dto.AdminPrayerRequest;
import com.hinduprayerlock.backend.model.dto.AdminPrayerResponse;
import com.hinduprayerlock.backend.repository.PrayerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminPrayerService {

    private final PrayerRepository prayerRepository;

    public List<AdminPrayerResponse> getAll(Mood mood) {
        List<PrayerEntity> prayers =
                (mood == null)
                        ? prayerRepository.findAll()
                        : prayerRepository.findByMood(mood);

        return prayers.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdminPrayerResponse create(AdminPrayerRequest request) {

        PrayerEntity prayer = new PrayerEntity();
        prayer.setMood(request.mood());
        prayer.setSanskrit(request.sanskrit());
        prayer.setEnglish(request.english());
        prayer.setHindi(request.hindi());
        prayer.setActive(true);

        return toResponse(prayerRepository.save(prayer));
    }

    @Transactional
    public AdminPrayerResponse update(UUID id, AdminPrayerRequest request) {

        PrayerEntity prayer = prayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prayer not found"));

        prayer.setMood(request.mood());
        prayer.setSanskrit(request.sanskrit());
        prayer.setEnglish(request.english());
        prayer.setHindi(request.hindi());

        return toResponse(prayer);
    }

    @Transactional
    public void disable(UUID id) {

        PrayerEntity prayer = prayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prayer not found"));

        prayer.setActive(false);
    }

    private AdminPrayerResponse toResponse(PrayerEntity p) {
        return new AdminPrayerResponse(
                p.getId(),
                p.getMood(),
                p.getSanskrit(),
                p.getEnglish(),
                p.getHindi(),
                p.isActive(),
                p.getCreatedAt()
        );
    }
}
