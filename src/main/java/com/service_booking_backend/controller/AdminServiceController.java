package com.service_booking_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.Service;
import com.service_booking_backend.entity.SubService;
import com.service_booking_backend.repository.ServiceRepository;
import com.service_booking_backend.repository.SubServiceRepository;

@RestController
@RequestMapping("/api/admin/services")
public class AdminServiceController {

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private SubServiceRepository subRepo;

    /* ================= SERVICES ================= */

    // Admin: get all services (active + disabled)
    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepo.findAll();
    }

    // Create new service
    @PostMapping
    public Service createService(@RequestBody Service s) {
        s.setActive(true);
        return serviceRepo.save(s);
    }

    // Disable service (soft delete)
    @PutMapping("/{id}/disable")
    public void disableService(@PathVariable("id") Long id) {
        Service s = serviceRepo.findById(id).orElseThrow();
        s.setActive(false);
        serviceRepo.save(s);

        // disable all sub services under this service
        subRepo.findByService_Id(id).forEach(sub -> {
            sub.setActive(false);
            subRepo.save(sub);
        });
    }

    // Enable service
    @PutMapping("/{id}/enable")
    public void enableService(@PathVariable("id") Long id) {
        Service s = serviceRepo.findById(id).orElseThrow();
        s.setActive(true);
        serviceRepo.save(s);

        subRepo.findByService_Id(id).forEach(sub -> {
            sub.setActive(true);
            subRepo.save(sub);
        });
    }

    /* ================= SUB SERVICES ================= */

    // Get all sub services for a service
    @GetMapping("/{serviceId}/sub-services")
    public List<SubService> getSubServices(@PathVariable("serviceId") Long serviceId) {
        return subRepo.findByService_Id(serviceId);
    }

    // Add new sub service to a service
    @PostMapping("/{serviceId}/sub-services")
    public SubService addSubService(
            @PathVariable("serviceId") Long serviceId,
            @RequestBody SubService sub
    ) {
        Service service = serviceRepo.findById(serviceId).orElseThrow();
        sub.setService(service);
        sub.setActive(true);
        return subRepo.save(sub);
    }

    // Disable sub service
    @PutMapping("/sub-services/{id}/disable")
    public void disableSub(@PathVariable("id") Long id) {
        SubService s = subRepo.findById(id).orElseThrow();
        s.setActive(false);
        subRepo.save(s);
    }

    // Enable sub service
    @PutMapping("/sub-services/{id}/enable")
    public void enableSub(@PathVariable("id") Long id) {
        SubService s = subRepo.findById(id).orElseThrow();
        s.setActive(true);
        subRepo.save(s);
    }

    // Update sub service (name + price)
    @PutMapping("/sub-services/{id}")
    public SubService updateSub(
            @PathVariable("id") Long id,
            @RequestBody SubService body
    ) {
        SubService s = subRepo.findById(id).orElseThrow();
        s.setName(body.getName());
        s.setPrice(body.getPrice());
        return subRepo.save(s);
    }
    
    @PutMapping("/{id}")
    public Service updateService(
            @PathVariable("id") Long id,
            @RequestBody Service body
    ) {
        Service s = serviceRepo.findById(id).orElseThrow();

        s.setName(body.getName());
        s.setDescription(body.getDescription());
        s.setImageUrl(body.getImageUrl());

        return serviceRepo.save(s);
    }
    
}
