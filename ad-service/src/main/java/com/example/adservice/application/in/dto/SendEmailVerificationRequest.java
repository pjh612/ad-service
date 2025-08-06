package com.example.adservice.application.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailVerificationRequest(
        @Email
        @NotBlank
        String email) {
}
