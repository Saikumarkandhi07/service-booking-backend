package com.service_booking_backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.dto.ChangePasswordRequest;
import com.service_booking_backend.dto.ForgotPasswordRequest;
import com.service_booking_backend.dto.LoginRequest;
import com.service_booking_backend.dto.RegisterRequest;
import com.service_booking_backend.dto.ResetPasswordRequest;
import com.service_booking_backend.entity.RefreshToken;
import com.service_booking_backend.entity.User;
import com.service_booking_backend.service.AuthService;
import com.service_booking_backend.service.RefreshTokenService;
import com.service_booking_backend.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /* ================= REGISTER ================= */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok("Registered successfully");
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        try {
            User user = authService.login(req);

            String jwt = jwtUtil.generateToken(user.getEmail());
            RefreshToken rt = refreshTokenService.createRefreshToken(user);

            return ResponseEntity.ok(
                Map.of(
                    "token", jwt,
                    "refreshToken", rt.getToken(),
                    "user", user
                )
            );

        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("USER_BLOCKED");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INVALID_CREDENTIALS");
        }
    }

    /* ================= REFRESH TOKEN ================= */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> req) {

        try {
            RefreshToken rt = refreshTokenService.verify(req.get("refreshToken"));
            String newJwt = jwtUtil.generateToken(rt.getUser().getEmail());
            return ResponseEntity.ok(Map.of("token", newJwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("SESSION_EXPIRED");
        }
    }

    /* ================= LOGOUT ================= */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();   // 🔥 FIX
        refreshTokenService.deleteByUser(user);

        return ResponseEntity.ok("Logged out successfully");
    }

    /* ================= CHANGE PASSWORD ================= */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest req,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();   // 🔥 FIX
        authService.changePassword(user, req);

        return ResponseEntity.ok("Password changed successfully");
    }

    /* ================= FORGOT PASSWORD ================= */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP sent successfully");
    }

    /* ================= RESET PASSWORD ================= */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful");
    }

    /* ================= RESEND OTP ================= */
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ForgotPasswordRequest request) {
        authService.resendOtp(request.getEmail());
        return ResponseEntity.ok("OTP resent successfully");
    }

    /* ================= SAVE PUSH TOKEN ================= */
    @PostMapping("/push-token")
    public ResponseEntity<?> savePushToken(
            @RequestBody Map<String, String> req,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();   // 🔥 FIX
        user.setPushToken(req.get("token"));
        authService.saveUser(user);

        return ResponseEntity.ok("Push token saved");
    }
}
