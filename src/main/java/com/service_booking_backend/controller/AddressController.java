package com.service_booking_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.Address;
import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.AddressRepository;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressRepository repo;

    /* ================= GET SAVED ADDRESSES ================= */
    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {

        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // 🔥 JWT already injected User
        User user = (User) auth.getPrincipal();

        List<Address> addresses = repo.findByUserId(user.getId());
        return ResponseEntity.ok(addresses);
    }

    /* ================= ADD ADDRESS ================= */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Address address, Authentication auth) {

        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = (User) auth.getPrincipal();

        address.setUserId(user.getId());

        return ResponseEntity.ok(repo.save(address));
    }

    /* ================= DELETE ADDRESS (SECURE) ================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {

        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = (User) auth.getPrincipal();

        Address address = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // ✅ SECURITY CHECK
        if (!address.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to delete this address");
        }

        repo.deleteById(id);
        return ResponseEntity.ok("Address deleted successfully");
    }
}
