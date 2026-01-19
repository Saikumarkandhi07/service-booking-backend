package com.service_booking_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RaiseTicketRequest {

	private String issueType;
    private String subject;
    private String description;
    private String priority;
}
