package com.hinduprayerlock.backend.utils;

import com.hinduprayerlock.backend.model.AuthUser;
import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("🔥 FILTER HIT: " + method + " " + path);

        String header = request.getHeader("Authorization");

        // ✅ No token → continue (important)
        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("❌ NO TOKEN → allowing request");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            System.out.println("✅ TOKEN FOUND");
            Claims claims = jwtUtil.validateToken(token);

            // ✅ Extract values
            String rawRole = claims.get("role", String.class);
            String email = claims.get("email", String.class);
            String subject = claims.getSubject();

            // ✅ Convert to UUID safely
            UUID userId = UUID.fromString(subject);

            // ✅ FIX: Safe role handling (VERY IMPORTANT)
            String role = rawRole;
            if (role != null && !role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            // ✅ Create user object
            AuthUser user = new AuthUser(
                    userId,
                    email,
                    role
            );

            // ✅ Create authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // ✅ Set authentication
            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (Exception e) {

            // ❌ Token invalid → clear context
            SecurityContextHolder.clearContext();
            System.out.println("❌ TOKEN INVALID: " + e.getMessage());


        }

        filterChain.doFilter(request, response);
    }
}