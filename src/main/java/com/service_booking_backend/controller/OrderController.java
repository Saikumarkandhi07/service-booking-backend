package com.service_booking_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.dto.CartItem;
import com.service_booking_backend.dto.OrderRequest;
import com.service_booking_backend.entity.Address;
import com.service_booking_backend.entity.Invoice;
import com.service_booking_backend.entity.Order;
import com.service_booking_backend.entity.OrderItem;
import com.service_booking_backend.entity.OrderStatus;
import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.AddressRepository;
import com.service_booking_backend.repository.InvoiceRepository;
import com.service_booking_backend.repository.OrderItemRepository;
import com.service_booking_backend.repository.OrderRepository;
import com.service_booking_backend.service.InvoiceService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final AddressRepository addressRepo;
    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepo;

    public OrderController(
            OrderRepository orderRepo,
            OrderItemRepository itemRepo,
            AddressRepository addressRepo,
            InvoiceService invoiceService,
            InvoiceRepository invoiceRepo
    ) {
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.addressRepo = addressRepo;
        this.invoiceService = invoiceService;
        this.invoiceRepo = invoiceRepo;
    }

    /* ================= SAVE ORDER AFTER PAYMENT ================= */
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveOrder(@RequestBody OrderRequest req, Authentication auth) {

        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = (User) auth.getPrincipal();

        if (req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Order items missing");
        }

        if (req.getAddressId() == null) {
            return ResponseEntity.badRequest().body("Address is required");
        }

        Address address = addressRepo.findById(req.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Address does not belong to user");
        }

        // 1️⃣ Save Order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(req.getTotalAmount());
        order.setPaymentId(req.getPaymentId());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setAddressId(req.getAddressId());

        Order savedOrder = orderRepo.save(order);

        // 2️⃣ Save Items
        for (CartItem item : req.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setServiceName(item.getName());
            oi.setPrice(item.getPrice());
            oi.setQty(item.getQty());
            itemRepo.save(oi);
        }

        // 3️⃣ Generate Invoice
        Invoice invoice = invoiceService.generateInvoice(savedOrder, req.getPaymentId());

        return ResponseEntity.ok(Map.of(
                "orderId", savedOrder.getId(),
                "invoiceId", invoice.getId(),
                "invoiceNumber", invoice.getInvoiceNumber(),
                "invoiceToken", invoice.getPublicToken()
        ));
    }

    /* ================= ORDER HISTORY ================= */
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(Authentication auth) {

        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                orderRepo.findByUser_IdOrderByCreatedAtDesc(user.getId())
        );
    }

    /* ================= GET SINGLE ORDER ================= */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable ("orderId") Long orderId, Authentication auth) {

        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) auth.getPrincipal();

        Order order = orderRepo
                .findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Invoice invoice = invoiceRepo.findByOrderId(order.getId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return ResponseEntity.ok(Map.of(
                "id", order.getId(),
                "status", order.getStatus(),
                "totalAmount", order.getTotalAmount(),
                "items", order.getItems(),
                "invoiceToken", invoice.getPublicToken()
        ));
    }

    /* ================= CANCEL ORDER ================= */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable("orderId") Long orderId, Authentication auth) {

        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = (User) auth.getPrincipal();

        Order order = orderRepo
                .findByIdAndUser_Id(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return ResponseEntity.badRequest().body("Already cancelled");
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            return ResponseEntity.badRequest().body("Completed orders cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);

        return ResponseEntity.ok("Order cancelled successfully");
    }
}
