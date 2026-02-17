package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sholak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Mood mood;

    private Integer durationSeconds;

    @Column(length = 2000)
    private String sanskrit;

    @Column(length = 2000)
    private String english;

    @Column(length = 2000)
    private String hindi;

    private String source;
}
