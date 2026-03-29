package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.model.Track;
import com.hinduprayerlock.backend.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;

    public List<Map<String, Object>> getTracks() {

        List<Track> tracks = trackRepository.findByIsActiveTrue();

        return tracks.stream().map(track -> {

            Map<String, Object> response = new HashMap<>();
            response.put("id", track.getId());
            response.put("name", track.getName());
            response.put("url", track.getUrl());
            response.put("duration", track.getDuration());

            return response;

        }).toList();
    }

    public Map<String, Object> getTrackById(UUID id) {

        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("id", track.getId());
        response.put("name", track.getName());
        response.put("url", track.getUrl());
        response.put("duration", track.getDuration());

        return response;
    }
}
