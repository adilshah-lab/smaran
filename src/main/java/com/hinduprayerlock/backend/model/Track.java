package com.hinduprayerlock.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String url;
    private int duration;
    private boolean isActive;
}
