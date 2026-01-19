package com.service_booking_backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {

	private String name;
    private String email;
    private String phone;
    private String password;
}
