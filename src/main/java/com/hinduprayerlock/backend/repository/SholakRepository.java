package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.Sholak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SholakRepository extends JpaRepository<Sholak, Long> {


    List<Sholak> findByMood(Mood mood);
}
