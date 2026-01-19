package com.service_booking_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {

	private String name;
	private int price;
	private int qty;
}
