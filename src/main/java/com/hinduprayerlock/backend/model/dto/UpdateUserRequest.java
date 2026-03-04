package com.hinduprayerlock.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String username;

    private String phoneNumber;
}
