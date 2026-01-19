package com.service_booking_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.SupportTicket;
import com.service_booking_backend.entity.TicketCategory;
import com.service_booking_backend.repository.SupportTicketRepository;

@RestController
@RequestMapping("/api/admin/support/login")
@CrossOrigin
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminLoginSupportController {

    @Autowired
    private SupportTicketRepository ticketRepo;

    /* ================= GET ALL LOGIN SUPPORT REQUESTS ================= */
    @GetMapping
    public ResponseEntity<?> getAllLoginSupportRequests() {

        List<SupportTicket> tickets =
            ticketRepo.findByCategoryOrderByCreatedAtDesc(
                TicketCategory.LOGIN_SUPPORT
            );

        return ResponseEntity.ok(tickets);
    }

    /* ================= RESPOND TO LOGIN SUPPORT REQUEST ================= */
    @PutMapping("/{id}/response")
    public ResponseEntity<?> respondToLoginSupport(
            @PathVariable Long id,
            @RequestBody SupportTicket body
    ) {

        SupportTicket ticket = ticketRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Support request not found"));

        ticket.setResponse(body.getResponse());
        ticket.setStatus(body.getStatus()); // OPEN / IN_PROGRESS / CLOSED

        ticketRepo.save(ticket);

        return ResponseEntity.ok("Login support request updated");
    }
}
