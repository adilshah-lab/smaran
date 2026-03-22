package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "verses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chapterNumber", "verseNumber"}))
public class Verse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int chapterNumber;
    private int verseNumber;
    private String verseLabel;

    @Column(columnDefinition = "TEXT")
    private String sanskrit;

    @Column(columnDefinition = "TEXT")
    private String hindi;

    @Column(columnDefinition = "TEXT")
    private String english;



}
