package com.service_booking_backend.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "support_ticket")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 RELATION TO USER
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String issueType;
    private String subject;

    @Column(length = 1000)
    private String description;

    private String priority;   // Low / Medium / High
    private String status;     // OPEN / IN_PROGRESS / CLOSED

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(length = 2000)
    private String response;
    
    
    @Column(nullable = false)
    private String email;   // for guest / login support

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

}
