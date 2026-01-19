package com.service_booking_backend.security;

import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole() != null ? user.getRole().name() : "USER";

        System.out.println("🔥 LOGIN USER: " + user.getEmail());
        System.out.println("🔥 ROLE FROM DB: " + role);
        System.out.println("🔥 ENABLED: " + user.isEnabled());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + role)
                .disabled(!user.isEnabled())
                .build();
    }

}
