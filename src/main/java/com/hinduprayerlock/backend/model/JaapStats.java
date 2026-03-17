package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "jaap_stats")
public class JaapStats {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private int totalJaap = 0;

    private int todayJaap = 0;
}