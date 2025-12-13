package com.emlakjet.purchaseinvoiceservice.repository;

import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.model.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {


    @Query("""
           SELECT COALESCE(SUM(i.amount), 0)
           FROM Invoice i
           WHERE i.purchasingSpecialist.email = :email
             AND i.status = 'APPROVED'
           """)
    BigDecimal sumApprovedAmountByEmail(@Param("email") String email);

    List<Invoice> findByStatus(InvoiceStatus status);

    boolean existsByBillNo(String billNo);


    @Query("""
            SELECT i FROM Invoice i
            WHERE (:status IS NULL OR i.status = :status)
            AND (:email IS NULL OR i.purchasingSpecialist.email = :email)
            AND (:firstName IS NULL OR i.purchasingSpecialist.firstName = :firstName)
            AND (:lastName IS NULL OR i.purchasingSpecialist.lastName = :lastName)
            """)
    List<Invoice> findAllWithFilters(String status, String email, String firstName, String lastName);
}