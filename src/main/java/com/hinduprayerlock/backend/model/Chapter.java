package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int chapterNumber;

    @Column(columnDefinition = "TEXT")
    private String chapterSummary;

    @Column(columnDefinition = "TEXT")
    private String chapterSummaryHindi;

    private String imageName;
    private int versesCount;


}
