package com.emlakjet.purchaseinvoiceservice.repository;

import com.emlakjet.purchaseinvoiceservice.model.entity.PurchasingSpecialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchasingSpecialistRepository
        extends JpaRepository<PurchasingSpecialist, Long> {

    Optional<PurchasingSpecialist> findByEmail(String email);

    boolean existsByEmail(String email);
}