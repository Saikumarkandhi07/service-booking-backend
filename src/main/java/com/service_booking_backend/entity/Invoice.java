package com.service_booking_backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private Long orderId;
    private Long userId;

    private double subtotal;
    private double gst;
    private double total;

    private String razorpayPaymentId;
    private String pdfPath;

    @Column(unique = true, nullable = false)
    private String publicToken;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean archived = false;

    private LocalDateTime archivedAt;

    public Invoice() {
        this.publicToken = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}
