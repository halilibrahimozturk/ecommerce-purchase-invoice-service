package com.emlakjet.purchaseinvoiceservice.exception;

import org.springframework.http.HttpStatus;

public class InvoiceOwnershipException extends BusinessException {

    public InvoiceOwnershipException() {
        super("Invoice can only be created with your own identity information",
                HttpStatus.FORBIDDEN,
                "INVOICE_OWNERSHIP_VIOLATION");
    }
}