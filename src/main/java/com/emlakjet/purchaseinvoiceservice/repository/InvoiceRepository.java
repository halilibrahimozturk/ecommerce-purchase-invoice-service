package com.emlakjet.purchaseinvoiceservice.repository;

import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    List<Invoice> findByStatus(InvoiceStatus status);

    @Query("""
            SELECT i FROM Invoice i
            WHERE (:status IS NULL OR i.status = :status)
            AND (:email IS NULL OR i.purchasingSpecialist.email = :email)
            AND (:firstName IS NULL OR i.purchasingSpecialist.firstName = :firstName)
            AND (:lastName IS NULL OR i.purchasingSpecialist.lastName = :lastName)
            """)
    List<Invoice> findAllWithFilters(String status, String email, String firstName, String lastName);
}