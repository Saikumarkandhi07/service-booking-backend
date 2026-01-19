package com.service_booking_backend.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;

}
