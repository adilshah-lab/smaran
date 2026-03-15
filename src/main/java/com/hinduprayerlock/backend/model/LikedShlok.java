package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "liked_shloks")
public class LikedShlok {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Integer shlokId;

    private LocalDateTime likedAt = LocalDateTime.now();
}
