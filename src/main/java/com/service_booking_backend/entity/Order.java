package com.service_booking_backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= USER ================= */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // prevent recursion
    private User user;

    /* ================= PAYMENT ================= */
    private double totalAmount;

    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; //PAID, CONFIRMED, CANCELLED, COMPLETED

    /* ================= DATE ================= */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /* ================= ITEMS ================= */
    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    /* ================= ADDRESS ================= */
    @Column(nullable = false)
    private Long addressId;
    
    
}
