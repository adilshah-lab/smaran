package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.AuthUser;
import com.hinduprayerlock.backend.model.DailyUserStats;
import com.hinduprayerlock.backend.service.UserActivityService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity")
public class UserActivityController {

    private final UserActivityService service;

    public UserActivityController(UserActivityService service) {
        this.service = service;
    }

    @PostMapping("/open")
    public void appOpened(@AuthenticationPrincipal AuthUser user) {
        service.markAppOpened(user.getId());
    }

    @PostMapping("/jaap")
    public void addJaap(@AuthenticationPrincipal AuthUser user,
                        @RequestParam int count) {
        service.addJaap(user.getId(), count);
    }

    @PostMapping("/meditation")
    public void meditation(@AuthenticationPrincipal AuthUser user,
                           @RequestParam int minutes) {
        service.addMeditation(user.getId(), minutes);
    }

    @PostMapping("/usage")
    public void usage(@AuthenticationPrincipal AuthUser user,
                      @RequestParam int minutes) {
        service.addUsage(user.getId(), minutes);
    }

    @GetMapping("/weekly")
    public List<DailyUserStats> getWeeklyStats(
            @AuthenticationPrincipal AuthUser user
    ) {
        return service.getWeekly(user.getId());
    }
}
