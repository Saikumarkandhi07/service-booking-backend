package com.service_booking_backend.dto;

import com.service_booking_backend.entity.User;

public class UserResponse {

	private Long id;
    private String name;
    private String email;
    private String role;
    private boolean enabled;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.enabled = user.isEnabled();
    }
}
