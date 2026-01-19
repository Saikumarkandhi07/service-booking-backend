package com.service_booking_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service_booking_backend.entity.User;
import com.service_booking_backend.entity.UserRole;
import com.service_booking_backend.repository.UserRepository;

@Service
public class AdminUserService {

	@Autowired
    private UserRepository userRepository;

    /* 🔒 BLOCK / UNBLOCK USER */
    public void updateUserStatus(Long userId, boolean enabled, String adminEmail) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ❌ Prevent admin from blocking themselves
        if (user.getEmail().equals(adminEmail)) {
            throw new RuntimeException("Admin cannot block themselves");
        }

        // ❌ Prevent blocking other admins (optional but recommended)
        if (user.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("Cannot block another admin");
        }

        user.setEnabled(enabled);
        userRepository.save(user);
    }

    /* 📋 GET ALL USERS */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
