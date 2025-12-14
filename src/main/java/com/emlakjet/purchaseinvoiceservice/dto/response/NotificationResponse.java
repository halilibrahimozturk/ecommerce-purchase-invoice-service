package com.emlakjet.purchaseinvoiceservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NotificationResponse(
        String invoiceId,
        String firstName,
        String lastName,
        String email,
        BigDecimal amount,
        String productName,
        String billNo,
        String message,
        LocalDateTime createdAt
) {
}