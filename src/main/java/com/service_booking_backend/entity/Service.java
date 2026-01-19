package com.service_booking_backend.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "services")
@Getter
@Setter
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private boolean active = true;

    // 🔥 THIS IS WHAT YOU MISSED
    @OneToMany(mappedBy = "service", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("service")
    private List<SubService> subServices;
}
