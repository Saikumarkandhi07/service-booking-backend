package com.service_booking_backend.controller;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.service_booking_backend.entity.Invoice;
import com.service_booking_backend.repository.InvoiceRepository;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepo;

    // 🔐 PUBLIC PDF VIEW (NO JWT REQUIRED)
    @GetMapping("/public/{token}")
    public ResponseEntity<byte[]> viewInvoice(
            @PathVariable("token") String token   // ✅ THIS FIXES EVERYTHING
    ) throws Exception {

        Invoice inv = invoiceRepo.findByPublicToken(token)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        File file = new File(inv.getPdfPath());

        if (!file.exists()) {
            throw new RuntimeException("Invoice PDF missing");
        }

        byte[] pdf = Files.readAllBytes(file.toPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + inv.getInvoiceNumber() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}
