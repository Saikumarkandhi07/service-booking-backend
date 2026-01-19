package com.service_booking_backend.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.UserRepository;
import com.service_booking_backend.util.JwtUtil;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip JWT for auth endpoints
        if (
            path.startsWith("/api/auth/login") ||
            path.startsWith("/api/auth/register") ||
            path.startsWith("/api/auth/refresh") ||   
            path.startsWith("/api/auth/logout") ||   
            path.startsWith("/api/auth/forgot-password") ||
            path.startsWith("/api/auth/reset-password") ||
            path.startsWith("/api/auth/resend-otp")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String email = jwtUtil.extractUsername(jwt);

            if (email == null) {
                sendUnauthorized(response, "TOKEN_INVALID");
                return;
            }

            // 🔥 Load user from DB
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null || !user.isEnabled()) {
                sendUnauthorized(response, "USER_BLOCKED");
                return;
            }

            // 🔥 Load UserDetails for JWT validation
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (!jwtUtil.validateToken(jwt, userDetails)) {
                sendUnauthorized(response, "TOKEN_EXPIRED");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // 🔥 Give BOTH ADMIN and ROLE_ADMIN
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(user.getRole().name()),               // ADMIN
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())      // ROLE_ADMIN
                );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                authorities
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }

        } catch (Exception e) {
            log.warn("JWT Error: {}", e.getMessage());
            sendUnauthorized(response, "TOKEN_INVALID");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter()
                .write("{\"message\":\"" + message + "\"}");
    }
}
