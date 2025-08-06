package com.example.adservice.common.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
