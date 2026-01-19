package com.service_booking_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardStatsResponse {

    private long totalUsers;
    private long blockedUsers;
    private long totalOrders;
    private double totalRevenue;
    private long openTickets;
}