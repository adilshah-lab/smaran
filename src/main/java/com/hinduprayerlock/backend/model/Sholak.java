package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Entity
public class Sholak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String title; // e.g., "Shloka 2"

    @Column(columnDefinition = "TEXT")
    private String sanskrit;

    @Column(name = "englishTranslation", columnDefinition = "TEXT")
    private String englishTranslation;

    @Column(name = "hindiTranslation", columnDefinition = "TEXT")
    private String hindiTranslation;


    private String source;

    @Enumerated(EnumType.STRING)
    private Mood mood;
}
