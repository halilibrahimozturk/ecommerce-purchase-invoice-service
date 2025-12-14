package com.emlakjet.purchaseinvoiceservice.exception;

import org.springframework.http.HttpStatus;

public class InvoiceCannotBeCancelledException extends BusinessException {

    public InvoiceCannotBeCancelledException(String reason) {
        super(reason,
                HttpStatus.BAD_REQUEST,
                "INVOICE_CANNOT_BE_CANCELLED");
    }
}