package com.service_booking_backend.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.Invoice;
import com.service_booking_backend.repository.InvoiceRepository;

@RestController
@RequestMapping("/api/admin/invoices")
@CrossOrigin
public class AdminInvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepo;

    // 🔐 ADMIN: All invoices (active + archived)
    @GetMapping
    public Page<Invoice> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return invoiceRepo.findAll(PageRequest.of(page, size));
    }

    // 🔐 ADMIN: Only archived invoices
    @GetMapping("/archived")
    public Page<Invoice> getArchivedInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return invoiceRepo.findByArchivedTrue(PageRequest.of(page, size));
    }

    // 🔐 ADMIN: Only active invoices
    @GetMapping("/active")
    public Page<Invoice> getActiveInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return invoiceRepo.findByArchivedFalse(PageRequest.of(page, size));
    }
}