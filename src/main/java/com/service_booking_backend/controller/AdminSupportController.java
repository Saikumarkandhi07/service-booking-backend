package com.service_booking_backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.dto.AdminDashboardStatsResponse;
import com.service_booking_backend.dto.AdminOrderResponse;
import com.service_booking_backend.dto.AdminTicketResponse;
import com.service_booking_backend.dto.AdminTicketUpdateRequest;
import com.service_booking_backend.dto.AdminUserResponse;
import com.service_booking_backend.entity.Invoice;
import com.service_booking_backend.entity.Order;
import com.service_booking_backend.entity.OrderItem;
import com.service_booking_backend.entity.OrderStatus;
import com.service_booking_backend.entity.SupportTicket;
import com.service_booking_backend.entity.TicketCategory;
import com.service_booking_backend.repository.InvoiceRepository;
import com.service_booking_backend.repository.OrderRepository;
import com.service_booking_backend.repository.SupportTicketRepository;
import com.service_booking_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/support")
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupportController {

    @Autowired
    private SupportTicketRepository ticketRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OrderRepository orderRepository;
    
    
    @Autowired
    private InvoiceRepository invoiceRepository;

    /* ================= SUPPORT TICKETS ================= */

    @GetMapping("/tickets")
    public ResponseEntity<?> getAllTickets() {

        List<AdminTicketResponse> response =
        		ticketRepo.findAppTickets(TicketCategory.LOGIN_SUPPORT)
                .stream()
                .map(t -> {
                    AdminTicketResponse dto = new AdminTicketResponse();
                    dto.setId(t.getId());
                    dto.setIssueType(t.getIssueType());
                    dto.setSubject(t.getSubject());
                    dto.setDescription(t.getDescription());
                    dto.setPriority(t.getPriority());
                    dto.setStatus(t.getStatus());
                    dto.setCreatedAt(t.getCreatedAt());

                    if (t.getUser() != null) {
                        dto.setUserName(t.getUser().getName());
                        dto.setUserEmail(t.getUser().getEmail());
                        dto.setUserPhone(t.getUser().getPhone());
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/tickets/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam String status
    ) {
        SupportTicket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(status);
        ticketRepo.save(ticket);

        return ResponseEntity.ok("Ticket status updated");
    }

    @PutMapping("/tickets/{id}/response")
    public ResponseEntity<?> updateTicketResponse(
            @PathVariable("id") Long id,
            @RequestBody AdminTicketUpdateRequest body
    ) {
        SupportTicket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(body.getStatus());
        ticket.setResponse(body.getResponse());
        ticketRepo.save(ticket);

        return ResponseEntity.ok("Ticket updated");
    }

    /* ================= USERS ================= */

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {

        List<AdminUserResponse> response =
            userRepo.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(u -> {
                    AdminUserResponse dto = new AdminUserResponse();

                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setPhone(u.getPhone());

                    // ✅ NULL SAFE ROLE (CRITICAL FIX)
                    dto.setRole(
                        u.getRole() != null ? u.getRole().name() : "USER"
                    );

                    dto.setEnabled(u.isEnabled());
                    dto.setCreatedAt(u.getCreatedAt());

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /* ================= ORDERS ================= */

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {

        List<AdminOrderResponse> response =
            orderRepository.findAllWithItems()
                .stream()
                .map(o -> {
                    AdminOrderResponse dto = new AdminOrderResponse();

                    dto.setOrderId(o.getId());
                    dto.setTotalAmount(o.getTotalAmount());
                    dto.setPaymentId(o.getPaymentId());
                    dto.setStatus(o.getStatus().name());
                    dto.setCreatedAt(o.getCreatedAt());

                    if (o.getUser() != null) {
                        dto.setUserName(o.getUser().getName());
                        dto.setUserEmail(o.getUser().getEmail());
                        dto.setUserPhone(o.getUser().getPhone());
                    }

                    dto.setServices(
                        o.getItems()
                         .stream()
                         .map(OrderItem::getServiceName)
                         .collect(Collectors.toList())
                    );

                    // 🔥 ADD ONLY THIS BLOCK
                    String invoiceToken = invoiceRepository
                            .findByOrderId(o.getId())
                            .map(Invoice::getPublicToken)
                            .orElse(null);

                    dto.setInvoiceToken(invoiceToken);

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    
    
    @PutMapping("/orders/{id}/complete")
    public void completeOrder(@PathVariable("id") Long id) {
        Order o = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (o.getStatus() == OrderStatus.CANCELLED)
            throw new RuntimeException("Cancelled order cannot be completed");

        o.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(o);
    }

    @PutMapping("/orders/{id}/cancel")
    public void cancelOrder(@PathVariable("id") Long id) {
        Order o = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (o.getStatus() == OrderStatus.COMPLETED)
            throw new RuntimeException("Completed order cannot be cancelled");

        o.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(o);
    }

    /* ================= BLOCK / UNBLOCK USER ================= */

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable("id") Long id,
            @RequestParam("enabled") boolean enabled,
            Authentication authentication
    ) {

        String adminEmail = authentication.getName();

        var user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmail().equals(adminEmail)) {
            return ResponseEntity.badRequest()
                    .body("Admin cannot block themselves");
        }

        if (user.getRole() != null && user.getRole().name().equals("ADMIN")) {
            return ResponseEntity.badRequest()
                    .body("Cannot block another admin");
        }

        user.setEnabled(enabled);
        userRepo.save(user);

        return ResponseEntity.ok("User status updated");
    }


    /* ================= DASHBOARD STATS ================= */

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {

        AdminDashboardStatsResponse dto = new AdminDashboardStatsResponse();

        dto.setTotalUsers(userRepo.count());
        dto.setBlockedUsers(userRepo.countByEnabled(false));
        dto.setTotalOrders(orderRepository.count());
        dto.setTotalRevenue(orderRepository.getTotalRevenue());
        dto.setOpenTickets(ticketRepo.countByStatus("OPEN"));

        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/login-support")
    public ResponseEntity<?> getLoginSupportRequests() {

        List<SupportTicket> tickets =
            ticketRepo.findByCategoryOrderByCreatedAtDesc(
                TicketCategory.LOGIN_SUPPORT
            );

        return ResponseEntity.ok(tickets);
    } 
    
    @PutMapping("/login-support/{id}/response")
    public ResponseEntity<?> respondLoginSupport(
            @PathVariable("id") Long id,
            @RequestBody AdminTicketUpdateRequest body
    ) {
        SupportTicket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getCategory() != TicketCategory.LOGIN_SUPPORT) {
            return ResponseEntity.badRequest().body("Invalid ticket category");
        }

        if (body.getResponse() == null || body.getResponse().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Response is required");
        }

        ticket.setStatus(
            body.getStatus() != null ? body.getStatus() : "CLOSED"
        );
        ticket.setResponse(body.getResponse().trim());

        ticketRepo.save(ticket);

        return ResponseEntity.ok("Login support response sent");
    }
    
}
