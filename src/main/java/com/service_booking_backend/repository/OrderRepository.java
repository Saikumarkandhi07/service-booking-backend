package com.service_booking_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.service_booking_backend.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // ================= USER ORDER HISTORY =================
    List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // ================= USER SINGLE ORDER =================
    Optional<Order> findByIdAndUser_Id(Long id, Long userId);

    // ================= ADMIN =================
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        LEFT JOIN FETCH o.items i
        ORDER BY o.createdAt DESC
    """)
    List<Order> findAllWithItems();

    // ================= REVENUE (COMPLETED ONLY) =================
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = com.service_booking_backend.entity.OrderStatus.COMPLETED
    """)
    double getTotalRevenue();
}
