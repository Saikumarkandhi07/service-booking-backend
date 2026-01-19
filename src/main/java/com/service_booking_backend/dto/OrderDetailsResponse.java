package com.service_booking_backend.dto;

import java.util.List;

import com.service_booking_backend.entity.OrderItem;

import lombok.Data;

@Data
public class OrderDetailsResponse {
    private Long id;
    private double totalAmount;
    private String status;
    private List<OrderItem> items;
    private String invoiceToken;
}
