package com.emlakjet.purchaseinvoiceservice.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
        String name,

        BigDecimal price
) {
}