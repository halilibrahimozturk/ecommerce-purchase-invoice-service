package com.emlakjet.purchaseinvoiceservice.exception;

public class ProductAlreadyExistsException extends BusinessException {

    public ProductAlreadyExistsException(String name) {
        super("Product with name '" + name + "' already exists");
    }
}