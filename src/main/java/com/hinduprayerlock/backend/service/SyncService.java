package com.hinduprayerlock.backend.service;

import com.hinduprayerlock.backend.ai.dto.SyncRequest;
import org.springframework.stereotype.Service;

@Service
public class SyncService {

    public void sync(SyncRequest request, String token){

        // TODO extract userId from JWT

        String userId = "demoUser";

        // Save liked shloks
        request.getLikedShloks()
                .forEach(id -> {

                    // save to DB
                });

        // Save jaap stats
        Integer total = request.getTotalJaap();
        Integer today = request.getTodayJaap();

        // update DB

    }
}
