package com.example.adadminservice.common.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
