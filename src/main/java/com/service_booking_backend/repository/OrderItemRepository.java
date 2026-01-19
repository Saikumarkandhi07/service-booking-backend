package com.service_booking_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service_booking_backend.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

	List<OrderItem> findByOrder_Id(Long orderId);
}
