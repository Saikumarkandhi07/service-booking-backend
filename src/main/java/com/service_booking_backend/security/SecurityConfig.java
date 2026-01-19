package com.service_booking_backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(jwtAuthEntryPoint)
            )

            .authorizeHttpRequests(auth -> auth

                // ================= PUBLIC =================
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Auth APIs
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/refresh",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/api/auth/resend-otp"
                ).permitAll()

                // 🔥 Public invoice preview
                .requestMatchers("/api/invoices/public/**").permitAll()

                // ✅ LOGIN / GUEST SUPPORT (NEW – REQUIRED)
                .requestMatchers("/api/support/login/**").permitAll()

                // Service browsing
                .requestMatchers("/api/services/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()

                // ================= ADMIN =================
                // Must use hasAuthority because JWT contains "ADMIN", not "ROLE_ADMIN"
                .requestMatchers("/api/admin/orders/**").hasAuthority("ADMIN")
                .requestMatchers("/api/admin/support/**").hasAuthority("ADMIN")
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                // ================= USER PROTECTED =================
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/invoices/**").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/addresses/**").authenticated()
                .requestMatchers("/api/support/**").authenticated()
                .requestMatchers("/api/notifications/**").authenticated()

                .anyRequest().authenticated()
            )

            // JWT filter
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
