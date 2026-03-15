package com.hinduprayerlock.backend.controller;


import com.hinduprayerlock.backend.ai.dto.UserStateResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserStateController {

    @GetMapping("/state")
    public UserStateResponse getState() {

        UserStateResponse response = new UserStateResponse();

        // TODO load from database

        response.setLikedShloks(List.of(1,5,9));
        response.setTotalJaap(118);
        response.setTodayJaap(50);

        return response;
    }
}
