package com.service_booking_backend.dto;

import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    @DecimalMin(value = "1.0", inclusive = true)
    private double totalAmount;

    private String paymentId;

    @NotEmpty
    private List<CartItem> items;

    @NotNull
    private Long addressId;
}
