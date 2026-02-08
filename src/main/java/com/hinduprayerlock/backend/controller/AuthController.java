package com.hinduprayerlock.backend.controller;


import com.hinduprayerlock.backend.ai.dto.RegisterRequest;
import com.hinduprayerlock.backend.model.dto.LoginRequest;
import com.hinduprayerlock.backend.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hinduprayerlock.backend.utils.JwtUtil;


import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/guest")
    public ResponseEntity<?> guestLogin() {

        String guestId = UUID.randomUUID().toString();

        String token = jwtUtil.generateToken(
                guestId,
                null,
                "GUEST"
        );

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "role", "GUEST"
                )
        );
    }
}
