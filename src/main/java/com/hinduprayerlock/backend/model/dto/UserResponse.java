package com.hinduprayerlock.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private String phoneNumber;

    private Boolean isSubscribed;

    private LocalDateTime createdAt;
}
