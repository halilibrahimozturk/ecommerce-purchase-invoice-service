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

    List<Invoice> findByStatusAndPurchasingSpecialist_Email(InvoiceStatus status, String email);

    boolean existsByBillNoAndStatus(String billNo, InvoiceStatus status);

}