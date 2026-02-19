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

        String header = request.getHeader("Authorization");

        // No token → continue without authentication
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            Claims claims = jwtUtil.validateToken(token);

            String role = claims.get("role", String.class);

            AuthUser user = new AuthUser(
                    claims.getSubject(),
                    claims.get("email", String.class),
                    role
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            // 🔥 VERY IMPORTANT LINE
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Clear context if token invalid
            SecurityContextHolder.clearContext();
        }

        // Continue filter chain ALWAYS
        filterChain.doFilter(request, response);
    }
}
