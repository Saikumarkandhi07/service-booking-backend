package com.service_booking_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service_booking_backend.entity.Service;

public interface ServiceRepository extends JpaRepository<Service, Long>{
	
	List<Service> findByNameContainingIgnoreCase(String name);
	
	List<Service> findByActiveTrue();

}
