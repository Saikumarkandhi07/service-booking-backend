package com.service_booking_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service_booking_backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	List<User> findAllByOrderByCreatedAtDesc();

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);
	
	long countByEnabled(boolean enabled);

}
