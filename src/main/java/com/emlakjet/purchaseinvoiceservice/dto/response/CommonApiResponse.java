package com.ozturk.purchaseinvoiceservice.dto.response;

import com.ozturk.purchaseinvoiceservice.model.ResponseStatus;
import lombok.Builder;

@Builder
public record CommonApiResponse<T>(
        ResponseStatus status,
        String message,
        T data
) {
    public static <T> CommonApiResponse<T> success(String message, T data) {
        return CommonApiResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> CommonApiResponse<T> error(String message) {
        return CommonApiResponse.<T>builder()
                .status(ResponseStatus.ERROR)
                .message(message)
                .data(null)
                .build();
    }
}