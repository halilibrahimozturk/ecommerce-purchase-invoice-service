package com.emlakjet.purchaseinvoiceservice.exception;

import org.springframework.http.HttpStatus;

public class InvoiceNotFoundException extends BusinessException {

    public InvoiceNotFoundException(Long id) {
        super(
                "Invoice not found with id: " + id,
                HttpStatus.NOT_FOUND,
                "INVOICE_NOT_FOUND"
        );
    }
}