package com.service_booking_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.Service;
import com.service_booking_backend.entity.SubService;
import com.service_booking_backend.service.ServiceDetailService;

@RestController
@RequestMapping("/api/services")   // 🔥 BASE PATH
@CrossOrigin
public class ServiceDetailController {

	@Autowired
    private ServiceDetailService service;

    @GetMapping("/{id}")
    public Service getService(@PathVariable("id") Long id) {
        return service.getService(id);
    }

    @GetMapping("/{id}/sub-services")
    public List<SubService> getSubServices(@PathVariable("id") Long id) {
        return service.getSubServices(id);
    }
    
    @GetMapping("/search")
    public List<Service> search(@RequestParam("q") String q) {
        return service.searchServices(q);
    }
    
}
