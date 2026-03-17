package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "liked_shloks")
public class LikedShlok {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private Integer shlokId;
}