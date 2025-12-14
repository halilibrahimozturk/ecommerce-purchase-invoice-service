package com.emlakjet.purchaseinvoiceservice.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(Long id) {
        super(
                "Product not found with id: " + id,
                HttpStatus.NOT_FOUND,
                "PRODUCT_NOT_FOUND"
        );
    }

    public ProductNotFoundException(String name) {
        super(
                "Product not found with name: " + name,
                HttpStatus.NOT_FOUND,
                "PRODUCT_NOT_FOUND"
        );
    }
}