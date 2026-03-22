package com.hinduprayerlock.backend.repository;

import com.hinduprayerlock.backend.model.Verse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerseRepository extends JpaRepository<Verse, Long> {

    List<Verse> findByChapterNumberOrderByVerseNumberAsc(int chapter);

    Optional<Verse> findFirstByChapterNumberAndVerseNumberGreaterThanOrderByVerseNumberAsc(
            int chapter, int verse
    );

    Optional<Verse> findByChapterNumberAndVerseNumber(int chapter, int verse);
}
