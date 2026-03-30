package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.service.TrackService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/track")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @GetMapping("/tracks")
    public ResponseEntity<?> getTracks() {
        return ResponseEntity.ok(trackService.getTracks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTrackById(@PathVariable UUID id) {
        return ResponseEntity.ok(trackService.getTrackById(id));
    }
}
