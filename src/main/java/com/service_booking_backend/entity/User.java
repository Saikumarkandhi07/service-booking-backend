package com.service_booking_backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phone;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 🔐 Forgot Password
    private String resetOtp;

    private LocalDateTime otpExpiry;
    
    private LocalDateTime lastOtpSentAt;
    
    // 🔔 PUSH NOTIFICATION TOKEN
    @Column(length = 255)
    private String pushToken;
    
    private boolean enabled = true; 
}
