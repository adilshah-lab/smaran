package com.hinduprayerlock.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ai_settings")
@Getter
@Setter
public class AiSettings {

    @Id
    private Long id = 1L;

    private boolean enabled = true;
    private int maxCallsPerDay = 100;
}
