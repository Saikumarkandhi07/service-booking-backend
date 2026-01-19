package com.service_booking_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.Notification;
import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.NotificationRepository;
import com.service_booking_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private UserRepository userRepo;

    /* 📥 Get notifications */
    @GetMapping
    public ResponseEntity<?> getNotifications(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                notificationRepo.findByUserOrderByCreatedAtDesc(user)
        );
    }

    /* ✅ Mark as read (SECURE) */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            Authentication auth
    ) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification n = notificationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // 🔒 SECURITY CHECK
        if (!n.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Not allowed");
        }

        n.setReadStatus(true);
        notificationRepo.save(n);

        return ResponseEntity.ok("Marked as read");
    }

    /* 🔴 Unread count */
    @GetMapping("/unread-count")
    public ResponseEntity<?> unreadCount(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);
        }

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                notificationRepo.countByUserAndReadStatusFalse(user)
        );
    }

    /* 🧹 Clear all */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearAll(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationRepo.deleteByUser(user);
        return ResponseEntity.ok("All notifications cleared");
    }
}
