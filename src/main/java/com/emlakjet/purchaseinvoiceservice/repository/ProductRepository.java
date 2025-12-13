package com.emlakjet.purchaseinvoiceservice.repository;

import com.emlakjet.purchaseinvoiceservice.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

}
