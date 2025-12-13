package com.emlakjet.purchaseinvoiceservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchasingSpecialists", uniqueConstraints = {@UniqueConstraint(columnNames = {"firstName", "lastName", "email"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchasingSpecialist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

}