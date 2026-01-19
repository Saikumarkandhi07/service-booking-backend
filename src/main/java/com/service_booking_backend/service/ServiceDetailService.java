package com.service_booking_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.service_booking_backend.entity.Service;
import com.service_booking_backend.entity.SubService;
import com.service_booking_backend.repository.ServiceRepository;
import com.service_booking_backend.repository.SubServiceRepository;

@org.springframework.stereotype.Service
public class ServiceDetailService {

	@Autowired
	private ServiceRepository serviceRepository;

	@Autowired
	private SubServiceRepository subServiceRepository;

	public Service getService(Long id) {
		return serviceRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Service not found with id " + id));
	}

	public List<SubService> getSubServices(Long id) {
		return subServiceRepository.findByService_Id(id);
	}

	public List<Service> searchServices(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return serviceRepository.findAll(); // show all if empty
		}
		return serviceRepository.findByNameContainingIgnoreCase(keyword);
	}
}
