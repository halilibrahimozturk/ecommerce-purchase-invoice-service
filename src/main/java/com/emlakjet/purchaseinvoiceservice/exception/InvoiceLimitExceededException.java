package com.emlakjet.purchaseinvoiceservice.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class InvoiceLimitExceededException extends BusinessException {

    public InvoiceLimitExceededException(BigDecimal currentTotal,
                                         BigDecimal invoiceAmount,
                                         BigDecimal maxLimit) {
        super(
                String.format(
                        "Invoice limit exceeded. Current total: %s, invoice amount: %s, max limit: %s",
                        currentTotal, invoiceAmount, maxLimit
                ),
                HttpStatus.BAD_REQUEST,
                "INVOICE_LIMIT_EXCEEDED"
        );
    }
}