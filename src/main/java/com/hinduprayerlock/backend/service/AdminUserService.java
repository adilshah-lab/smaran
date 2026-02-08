package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.dto.AdminUserResponse;
import com.hinduprayerlock.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    public List<AdminUserResponse> getUsers() {

        return userRepository.getUsersWithUsageRaw()
                .stream()
                .map(row -> new AdminUserResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (LocalDateTime) row[2],
                        (Long) row[3]
                ))
                .toList();
    }
}
