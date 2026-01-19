package com.service_booking_backend.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.service_booking_backend.repository.InvoiceRepository;

@Component
@EnableScheduling
public class InvoiceArchiver {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
    @Transactional
    public void archiveInvoices() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        invoiceRepo.archiveOldInvoices(sixMonthsAgo);
    }
}
