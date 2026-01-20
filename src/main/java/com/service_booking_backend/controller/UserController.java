package com.service_booking_backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* ================= GET PROFILE ================= */
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User authUser = (User) authentication.getPrincipal();

        // 🔥 FETCH FRESH USER FROM Database
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("mobile", user.getPhone());
        response.put("role", user.getRole() != null ? user.getRole().name() : null);

        return ResponseEntity.ok(response);
    }

    /* ================= UPDATE PROFILE ================= */
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();

        user.setName(body.get("name"));
        user.setPhone(body.get("mobile"));

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }
}
