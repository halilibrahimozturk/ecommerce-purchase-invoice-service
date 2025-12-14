package com.emlakjet.purchaseinvoiceservice.exception;

import org.springframework.http.HttpStatus;

public class ProductAlreadyExistsException extends BusinessException {

    public ProductAlreadyExistsException(String name) {
        super(
                "Product already exists with name: " + name,
                HttpStatus.CONFLICT,
                "PRODUCT_ALREADY_EXISTS"
        );
    }
}