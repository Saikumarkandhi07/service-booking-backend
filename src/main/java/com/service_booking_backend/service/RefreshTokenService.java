package com.service_booking_backend.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service_booking_backend.entity.RefreshToken;
import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository repo;

    public RefreshToken createRefreshToken(User user) {
        repo.deleteByUserId(user.getId());   // 🔥 now works

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiryDate(LocalDateTime.now().plusDays(30));

        return repo.save(rt);
    }

    public RefreshToken verify(String token) {
        if (token == null || token.isBlank()) return null;

        RefreshToken rt = repo.findByToken(token).orElse(null);
        if (rt == null) return null;

        if (rt.getExpiryDate().isBefore(LocalDateTime.now())) {
            repo.delete(rt);
            return null;
        }

        return rt;
    }

    public void deleteByUser(User user) {
        repo.deleteByUserId(user.getId());   // 🔥 now deletes
    }
}