package com.example.adservice.application.in.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record SignupRequest(
        String id,
        String password,
        String name,
        String email,
        String businessNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
        LocalDate startAt,
        String emailVerificationToken,
        String businessNumberVerificationToken
) {
}
