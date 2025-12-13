package com.emlakjet.purchaseinvoiceservice.exception;

public class InvoiceNotFoundException extends BusinessException {

    public InvoiceNotFoundException(Long id) {
        super("Invoice not found with id: " + id);
    }
}