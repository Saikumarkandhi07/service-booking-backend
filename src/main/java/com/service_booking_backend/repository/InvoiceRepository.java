package com.service_booking_backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.service_booking_backend.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

  
    Optional<Invoice> findByOrderId(Long orderId);
    Optional<Invoice> findByPublicToken(String publicToken);

    // Archive invoices older than given date
    @Modifying
    @Query("""
        UPDATE Invoice i
        SET i.archived = true,
            i.archivedAt = CURRENT_TIMESTAMP
        WHERE i.archived = false
          AND i.createdAt < :cutoff
    """)
    void archiveOldInvoices(LocalDateTime cutoff);
    
    
    Page<Invoice> findAll(Pageable pageable);

    Page<Invoice> findByArchivedTrue(Pageable pageable);

    Page<Invoice> findByArchivedFalse(Pageable pageable);
}
