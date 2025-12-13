package com.emlakjet.purchaseinvoiceservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InvoiceRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @Email
        @NotBlank
        String email,

        @NotNull
        BigDecimal amount,

        @NotBlank
        String productName,

        @NotBlank
        String billNo
) {
}
