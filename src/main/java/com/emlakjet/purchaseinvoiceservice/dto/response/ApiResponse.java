package com.emlakjet.purchaseinvoiceservice.dto.response;

import com.emlakjet.purchaseinvoiceservice.model.ResponseStatus;
import lombok.Builder;

@Builder
public record ApiResponse<T>(
        ResponseStatus status,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.ERROR)
                .message(message)
                .data(null)
                .build();
    }
}