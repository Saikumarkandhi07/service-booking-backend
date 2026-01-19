package com.service_booking_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.service_booking_backend.entity.SupportTicket;
import com.service_booking_backend.entity.TicketCategory;
import com.service_booking_backend.entity.User;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

	/* 👤 USER → OWN TICKETS */
    List<SupportTicket> findByUserOrderByCreatedAtDesc(User user);

    /* 👑 ADMIN → ALL TICKETS (OLD – keep it) */
    List<SupportTicket> findAllByOrderByCreatedAtDesc();

    @Query("""
    	    SELECT t FROM SupportTicket t
    	    WHERE t.category IS NULL
    	       OR t.category <> :category
    	    ORDER BY t.createdAt DESC
    	""")
    	List<SupportTicket> findAppTickets(
    	    @org.springframework.data.repository.query.Param("category")
    	    TicketCategory category
    	);

    long countByStatus(String status);

    List<SupportTicket> findByEmailAndCategoryOrderByCreatedAtDesc(
        String email,
        TicketCategory category
    );

    /* 🔐 LOGIN SUPPORT (UNCHANGED) */
    List<SupportTicket> findByCategoryOrderByCreatedAtDesc(
        TicketCategory category
    );
}
