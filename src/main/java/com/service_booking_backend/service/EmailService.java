package com.service_booking_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Service Booking - Password Reset OTP");
        message.setText(
            "Your OTP for password reset is: " + otp +
            "\n\nThis OTP is valid for 10 minutes."
        );

        mailSender.send(message);
    }
}
