package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal AuthUser user
    ) {
        UserEntity entity = userService.getOrCreateUser(user);

        return ResponseEntity.ok(
                Map.of(
                        "id", entity.getId(),
                        "email", entity.getEmail(),
                        "createdAt", entity.getCreatedAt()
                )
        );
    }
}

