package com.example.adservice.application.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SendEmailVerificationRequest(
        @Email
        @NotNull
        String email) {
}
