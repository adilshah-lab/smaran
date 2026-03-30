package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.Mood;
import com.hinduprayerlock.backend.model.dto.AdminPrayerRequest;
import com.hinduprayerlock.backend.model.dto.AdminPrayerResponse;
import com.hinduprayerlock.backend.service.AdminPrayerService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/prayers")
@RequiredArgsConstructor
public class AdminPrayerController {

    private final AdminPrayerService adminPrayerService;

    @GetMapping
    public ResponseEntity<List<AdminPrayerResponse>> getAll(
            @RequestParam(required = false) Mood mood
    ) {
        return ResponseEntity.ok(
                adminPrayerService.getAll(mood)
        );
    }

    @PostMapping
    public ResponseEntity<AdminPrayerResponse> create(
            @Valid @RequestBody AdminPrayerRequest request
    ) {
        return ResponseEntity.ok(
                adminPrayerService.create(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminPrayerResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody AdminPrayerRequest request
    ) {
        return ResponseEntity.ok(
                adminPrayerService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> disable(@PathVariable UUID id) {
        adminPrayerService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
