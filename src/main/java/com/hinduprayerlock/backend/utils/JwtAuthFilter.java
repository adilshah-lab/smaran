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

        System.out.println("\n================ JWT FILTER START ================");
        System.out.println("➡️ Path: " + path);
        System.out.println("➡️ Method: " + method);

        String header = request.getHeader("Authorization");
        System.out.println("➡️ Authorization Header: " + header);

        // ✅ No token
        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("⚠️ No token found → continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            System.out.println("✅ Token extracted");

            Claims claims = jwtUtil.validateToken(token);
            System.out.println("✅ Token validated successfully");

            // ✅ Extract values
            String rawRole = claims.get("role", String.class);
            String email = claims.get("email", String.class);
            String subject = claims.getSubject();

            System.out.println("👉 Subject (UserId): " + subject);
            System.out.println("👉 Email: " + email);
            System.out.println("👉 Raw Role: " + rawRole);

            // ✅ Convert UUID
            UUID userId = UUID.fromString(subject);

            // ✅ Role fix
            String role = rawRole;
            if (role != null && !role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            System.out.println("👉 Final Role: " + role);

            // ✅ Create user
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

            // ✅ Set context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("✅ Authentication set in SecurityContext");

        } catch (Exception e) {

            System.out.println("❌ TOKEN ERROR: " + e.getMessage());

            SecurityContextHolder.clearContext();

            // 🔥 IMPORTANT FIX → return 403 immediately
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or Expired Token");
            return;
        }

        System.out.println("➡️ Continuing filter chain...");
        System.out.println("================ JWT FILTER END ================\n");

        filterChain.doFilter(request, response);
    }
}