package com.hinduprayerlock.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.hinduprayerlock.backend.ai.dto.AuthResponse;
import com.hinduprayerlock.backend.ai.dto.GoogleAuthRequest;
import com.hinduprayerlock.backend.ai.dto.LoginResponse;
import com.hinduprayerlock.backend.ai.dto.RegisterRequest;
import com.hinduprayerlock.backend.exceptions.EmailAlreadyExistsException;
import com.hinduprayerlock.backend.exceptions.InvalidCredentialsException;
import com.hinduprayerlock.backend.exceptions.ResourceNotFoundException;
import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.LoginRequest;
import com.hinduprayerlock.backend.model.dto.UpdateUserRequest;
import com.hinduprayerlock.backend.model.dto.UserResponse;
import com.hinduprayerlock.backend.repository.UserRepository;
import com.hinduprayerlock.backend.utils.GoogleTokenVerifier;
import com.hinduprayerlock.backend.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

//        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
//            throw new PhoneAlreadyExistsException("Phone number already registered");
//        }

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
//        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsSubscribed(false);
        user.setRole("USER");

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(token, user.getUsername(), user.getCreatedAt());
    }

    public LoginResponse login(LoginRequest request) {

        if (request.getIdentifier() == null || request.getIdentifier().isBlank()) {
            throw new InvalidCredentialsException("Identifier is required");
        }

        UserEntity user = userRepository
                .findByEmailOrPhoneNumber(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    public UserResponse getUser(UUID userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToResponse(user);
    }


    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername().trim());
        }

//        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
//
//            Optional<UserEntity> existingUser =
//                    userRepository.findByPhoneNumber(request.getPhoneNumber());
//
//            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
//                throw new PhoneAlreadyExistsException("Phone number already in use");
//            }
//
//            user.setPhoneNumber(request.getPhoneNumber().trim());
//        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(UserEntity user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
//                user.getPhoneNumber(),
                user.getIsSubscribed(),
                user.getCreatedAt()
        );

    }

    public LoginResponse googleLogin(GoogleAuthRequest request) {

        String email;
        String name;

        try {

            // =========================
            // ANDROID FLOW (idToken)
            // =========================
            if (request.getIdToken() != null && !request.getIdToken().isBlank()) {

                GoogleIdToken.Payload payload =
                        googleTokenVerifier.verify(request.getIdToken());

                if (payload == null) {
                    throw new InvalidCredentialsException("Invalid Google ID token");
                }

                email = payload.getEmail();
                name = (String) payload.get("name");

                Boolean emailVerified = payload.getEmailVerified();

                if (emailVerified == null || !emailVerified) {
                    throw new InvalidCredentialsException("Email not verified by Google");
                }
            }

            // =========================
            // EXTENSION FLOW (accessToken)
            // =========================
            else if (request.getAccessToken() != null && !request.getAccessToken().isBlank()) {

                String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token="
                        + request.getAccessToken();

                java.net.URL obj = new java.net.URL(url);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();

                if (responseCode != 200) {
                    throw new InvalidCredentialsException("Invalid Google access token");
                }

                java.io.BufferedReader in = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream())
                );

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                org.json.JSONObject json = new org.json.JSONObject(response.toString());

                // 🔐 VERIFY CLIENT ID
                String aud = json.getString("aud");

                if (!aud.equals(googleClientId)) {
                    throw new InvalidCredentialsException("Token not issued for this app");
                }

                email = json.getString("email");
                name = json.optString("name", "User");

                // 🔐 CHECK EMAIL VERIFIED
                boolean emailVerified = json.optBoolean("email_verified", false);

                if (!emailVerified) {
                    throw new InvalidCredentialsException("Email not verified by Google");
                }
            }

            // =========================
            // NO TOKEN PROVIDED
            // =========================
            else {
                throw new InvalidCredentialsException("Google token is required");
            }

            // =========================
            // USER HANDLING WITH FLOW
            // =========================
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

            UserEntity user;

            String flow = request.getFlow(); // LOGIN or REGISTER

            // =========================
            // 🚀 REGISTER FLOW
            // =========================
            if ("REGISTER".equalsIgnoreCase(flow)) {

                if (optionalUser.isPresent()) {
                    throw new EmailAlreadyExistsException("User already registered. Please login.");
                }

                user = new UserEntity();
                user.setId(UUID.randomUUID());
                user.setEmail(email);

                // ✅ Only first name
                String firstName = (name != null && !name.isBlank())
                        ? name.split(" ")[0]
                        : "User";

                user.setUsername(firstName);
                user.setPassword(null);
                user.setProvider("GOOGLE");
                user.setCreatedAt(LocalDateTime.now());
                user.setIsSubscribed(false);
                user.setRole("USER");

                userRepository.save(user);
            }

            // =========================
            // 🔐 LOGIN FLOW
            // =========================
            else if ("LOGIN".equalsIgnoreCase(flow)) {

                if (optionalUser.isEmpty()) {
                    throw new ResourceNotFoundException("User not registered. Please register first.");
                }

                user = optionalUser.get();

                // 🔐 Prevent wrong provider login
                if (user.getProvider() != null && !user.getProvider().equals("GOOGLE")) {
                    throw new InvalidCredentialsException("Please login using email/password");
                }
            }

            // =========================
            // ❌ INVALID FLOW
            // =========================
            else {
                throw new InvalidCredentialsException("Invalid flow. Use LOGIN or REGISTER");
            }

            // =========================
            // JWT GENERATION
            // =========================
            String token = jwtUtil.generateToken(
                    user.getId().toString(),
                    user.getEmail(),
                    user.getRole()
            );

            return new LoginResponse(
                    token,
                    user.getUsername(),
                    user.getEmail(),
                    user.getCreatedAt()
            );

        } catch (java.io.IOException e) {
            throw new InvalidCredentialsException("Error communicating with Google");
        }
        catch (Exception e) {
            throw e; // preserve custom exceptions
        }
    }
}
