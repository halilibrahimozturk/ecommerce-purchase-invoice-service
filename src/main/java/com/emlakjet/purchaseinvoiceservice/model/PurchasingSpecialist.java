package com.emlakjet.purchaseinvoiceservice.model;

import com.emlakjet.purchaseinvoiceservice.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchasingSpecialists")
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
    private String email;

}