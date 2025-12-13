package com.emlakjet.purchaseinvoiceservice.exception;

import java.math.BigDecimal;

public class InvoiceLimitExceededException extends BusinessException {

    public InvoiceLimitExceededException(BigDecimal limit, BigDecimal currentTotal) {
        super("Invoice rejected. Limit: " + limit + ", current approved total: " + currentTotal);
    }
}