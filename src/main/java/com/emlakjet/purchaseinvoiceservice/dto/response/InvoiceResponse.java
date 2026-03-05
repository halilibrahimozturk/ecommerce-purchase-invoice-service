package com.ozturk.purchaseinvoiceservice.dto.response;

import com.ozturk.purchaseinvoiceservice.model.InvoiceStatus;

import java.math.BigDecimal;

public record InvoiceResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        BigDecimal amount,
        String productName,
        String billNo,
        InvoiceStatus status
) {
}