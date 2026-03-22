package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.model.dto.AdminUserResponse;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping
    public List<AdminUserResponse> getAll() {
        return service.getUsers();
    }
}
