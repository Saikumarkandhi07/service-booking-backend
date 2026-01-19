package com.service_booking_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.service_booking_backend.entity.Notification;
import com.service_booking_backend.entity.User;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUserOrderByCreatedAtDesc(User user);
	
	long countByUserAndReadStatusFalse(User user);

    void deleteByUser(User user);
}
