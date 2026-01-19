package com.service_booking_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Linked to logged-in user
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 10)
    private String pincode;

    // ✅ NEW: Default address flag
    @Column(nullable = false)
    private boolean isDefault = false;
}
