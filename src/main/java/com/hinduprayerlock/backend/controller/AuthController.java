package com.hinduprayerlock.backend.controller;


import com.hinduprayerlock.backend.ai.dto.AuthResponse;
import com.hinduprayerlock.backend.ai.dto.GoogleAuthRequest;
import com.hinduprayerlock.backend.ai.dto.LoginResponse;
import com.hinduprayerlock.backend.ai.dto.RegisterRequest;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.LoginRequest;
import com.hinduprayerlock.backend.model.dto.UpdateUserRequest;
import com.hinduprayerlock.backend.model.dto.UserResponse;
import com.hinduprayerlock.backend.service.AuthService;

import jakarta.validation.Valid;
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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/guest")
    public ResponseEntity<?> guestLogin() {

        String userId = UUID.randomUUID().toString();

        // 🔥 Generate safe dummy email for guest
        String email = "guest_" + userId + "@smaraan.com";

        String token = jwtUtil.generateToken(userId, email, "GUEST");

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "role", "GUEST"
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");

        String userId = jwtUtil.extractUserId(jwt);

        return ResponseEntity.ok(
                authService.getUser(UUID.fromString(userId))
        );
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateUserRequest request
    ) {

        String jwt = token.replace("Bearer ", "");

        String userId = jwtUtil.extractUserId(jwt);

        UserResponse user = authService.updateUser(UUID.fromString(userId), request);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(
            @RequestBody GoogleAuthRequest request
    ) {
        return ResponseEntity.ok(
                authService.googleLogin(request.getIdToken())
        );
    }
}
