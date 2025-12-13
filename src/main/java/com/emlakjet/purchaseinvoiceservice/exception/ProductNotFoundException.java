package com.emlakjet.purchaseinvoiceservice.exception;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

    public ProductNotFoundException(String productName) {
        super("Product not found with name: " + productName);
    }
}