package com.hinduprayerlock.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_prayer_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "prayer_id"})
)
@Getter
@Setter
public class UserPrayerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "prayer_id", nullable = false)
    private UUID prayerId;

    @Column(nullable = false)
    private LocalDateTime servedAt;
}
