package com.service_booking_backend.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.service_booking_backend.dto.ChangePasswordRequest;
import com.service_booking_backend.dto.LoginRequest;
import com.service_booking_backend.dto.RegisterRequest;
import com.service_booking_backend.dto.ResetPasswordRequest;
import com.service_booking_backend.entity.User;
import com.service_booking_backend.entity.UserRole;
import com.service_booking_backend.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    /* ================= REGISTER ================= */
    public void register(RegisterRequest req) {

        if (repo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail().trim());
        u.setPhone(req.getPhone());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setRole(UserRole.USER);
        u.setEnabled(true);

        repo.save(u);
    }

    /* ================= LOGIN ================= */
    public User login(LoginRequest req) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                req.getEmail().trim(),
                req.getPassword()
            )
        );

        return repo.findByEmail(req.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /* ================= CHANGE PASSWORD (🔥 FIXED) ================= */
    public void changePassword(User user, ChangePasswordRequest req) {

        if (!encoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(encoder.encode(req.getNewPassword()));
        repo.save(user);
    }

    /* ================= FORGOT PASSWORD ================= */
    public void forgotPassword(String email) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        user.setResetOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        repo.save(user);
        emailService.sendOtp(email, otp);
    }

    /* ================= RESET PASSWORD ================= */
    public void resetPassword(ResetPasswordRequest request) {

        User user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getResetOtp() == null ||
            !user.getResetOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        user.setOtpExpiry(null);

        repo.save(user);
    }

    /* ================= RESEND OTP ================= */
    public void resendOtp(String email) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getLastOtpSentAt() != null &&
            user.getLastOtpSentAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Please wait before requesting another OTP");
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        user.setResetOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setLastOtpSentAt(LocalDateTime.now());

        repo.save(user);
        emailService.sendOtp(email, otp);
    }

    /* ================= SAVE USER (used by push-token) ================= */
    public void saveUser(User user) {
        repo.save(user);
    }
}
