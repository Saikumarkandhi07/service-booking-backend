package com.service_booking_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.dto.RaiseTicketRequest;
import com.service_booking_backend.entity.SupportTicket;
import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.SupportTicketRepository;

@RestController
@RequestMapping("/api/support")
@CrossOrigin
public class SupportTicketController {

    @Autowired
    private SupportTicketRepository ticketRepo;

    /* ================= RAISE TICKET ================= */
    @PostMapping("/tickets")
    public ResponseEntity<?> raiseTicket(
            @RequestBody RaiseTicketRequest req,
            Authentication auth
    ) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

       
        User user = (User) auth.getPrincipal();

        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setEmail(user.getEmail());
        ticket.setIssueType(req.getIssueType());
        ticket.setSubject(req.getSubject());
        ticket.setDescription(req.getDescription());
        ticket.setPriority(req.getPriority());
        ticket.setStatus("OPEN");

        return ResponseEntity.ok(ticketRepo.save(ticket));
    }

    /* ================= USER TICKETS ================= */
    @GetMapping("/tickets")
    public ResponseEntity<?> myTickets(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                ticketRepo.findByUserOrderByCreatedAtDesc(user)
        );
    }
}
