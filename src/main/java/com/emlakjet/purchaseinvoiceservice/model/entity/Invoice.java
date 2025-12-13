package com.emlakjet.purchaseinvoiceservice.model.entity;

import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;
import com.emlakjet.purchaseinvoiceservice.model.PurchasingSpecialist;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private String billNo;
    private boolean approved;

    @ManyToOne
    private PurchasingSpecialist purchasingSpecialist;

    @ManyToOne
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

}