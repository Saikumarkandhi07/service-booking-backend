package com.service_booking_backend.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminTicketResponse {

    private Long id;
    private String issueType;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;

    private String userName;
    private String userEmail;
    private String userPhone;

}
