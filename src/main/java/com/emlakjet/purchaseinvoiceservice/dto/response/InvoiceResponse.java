package com.emlakjet.purchaseinvoiceservice.dto.response;

import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;

import java.math.BigDecimal;

public record InvoiceResponse(
        String id,
        InvoiceStatus status,
        String message,
        BigDecimal amount,
        String billNo
) {
}