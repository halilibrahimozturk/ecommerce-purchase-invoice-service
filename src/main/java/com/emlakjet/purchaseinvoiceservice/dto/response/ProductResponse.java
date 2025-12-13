package com.emlakjet.purchaseinvoiceservice.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price
) {
}