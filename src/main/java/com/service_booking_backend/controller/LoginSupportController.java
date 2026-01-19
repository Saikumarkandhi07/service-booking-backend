package com.service_booking_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.SupportTicket;
import com.service_booking_backend.entity.TicketCategory;
import com.service_booking_backend.repository.SupportTicketRepository;

@RestController
@RequestMapping("/api/support/login")
@CrossOrigin
public class LoginSupportController {

    @Autowired
    private SupportTicketRepository ticketRepo;

    /* ================= CREATE LOGIN SUPPORT TICKET ================= */
    @PostMapping
    public ResponseEntity<?> raiseLoginTicket(@RequestBody SupportTicket body) {

        if (body.getEmail() == null || body.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (body.getDescription() == null || body.getDescription().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Description is required");
        }

        SupportTicket ticket = new SupportTicket();

        // ✅ LOGIN SUPPORT = NO USER
        ticket.setUser(null);

        ticket.setEmail(body.getEmail().trim());
        ticket.setSubject(
            body.getSubject() != null && !body.getSubject().trim().isEmpty()
                ? body.getSubject().trim()
                : "Login Issue"
        );
        ticket.setDescription(body.getDescription().trim());

        ticket.setPriority("HIGH");
        ticket.setStatus("OPEN");
        ticket.setCategory(TicketCategory.LOGIN_SUPPORT);

        ticketRepo.save(ticket);

        return ResponseEntity.ok("Login support request submitted successfully");
    }

    /* ================= TRACK LOGIN SUPPORT TICKETS ================= */
    @GetMapping
    public ResponseEntity<?> trackTicket(
            @RequestParam(name = "email") String email
    ) {

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        List<SupportTicket> tickets =
            ticketRepo.findByEmailAndCategoryOrderByCreatedAtDesc(
                email.trim(),
                TicketCategory.LOGIN_SUPPORT
            );

        return ResponseEntity.ok(tickets);
    }
}
