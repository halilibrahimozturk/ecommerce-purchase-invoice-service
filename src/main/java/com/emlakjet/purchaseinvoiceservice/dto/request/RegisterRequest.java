package com.emlakjet.purchaseinvoiceservice.dto.request;

import com.emlakjet.purchaseinvoiceservice.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull UserRole role

) {}