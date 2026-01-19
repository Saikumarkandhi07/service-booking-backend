package com.service_booking_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service_booking_backend.entity.SubService;

public interface SubServiceRepository extends JpaRepository<SubService, Long> {

	List<SubService> findByService_Id(Long serviceId);
	
	 List<SubService> findByServiceIdAndActiveTrue(Long serviceId);
}
