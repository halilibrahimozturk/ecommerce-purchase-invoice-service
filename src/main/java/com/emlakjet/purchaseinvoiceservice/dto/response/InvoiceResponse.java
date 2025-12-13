package com.emlakjet.purchaseinvoiceservice.dto.response;

import com.emlakjet.purchaseinvoiceservice.model.InvoiceStatus;

import java.math.BigDecimal;

public record InvoiceResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        BigDecimal amount,
        String productName,
        String billNo,
        InvoiceStatus status,
        String message

) {
}