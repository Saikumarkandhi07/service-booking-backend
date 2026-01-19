package com.service_booking_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.service_booking_backend.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

	List<Address> findByUserId(Long userId);
}
