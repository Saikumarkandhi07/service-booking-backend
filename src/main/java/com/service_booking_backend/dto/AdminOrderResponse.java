package com.service_booking_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderResponse {

    private Long orderId;

    // ✅ MUST MATCH Order entity
    private double totalAmount;

    private String paymentId;
    private String status;
    private LocalDateTime createdAt;

    // 👤 User details
    private String userName;
    private String userEmail;
    private String userPhone;
    
 // 🛠 NEW → SERVICE NAMES
    private List<String> services;
    
    private String invoiceToken;
}
