package com.example.adservice.application.in.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record SignupRequest(
        String id,
        String password,
        String name,
        String email,
        String businessNumber,
        @DateTimeFormat(pattern = "yyyyMMdd")
        LocalDate startAt,
        String emailVerificationToken,
        String businessNumberVerificationToken
) {
}
