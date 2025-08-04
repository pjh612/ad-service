package com.example.adservice.application.in;

import com.example.adservice.application.in.dto.SendEmailVerificationRequest;
import com.example.adservice.application.in.dto.VerifyEmailResponse;

public interface EmailVerificationUseCase {
    void sendEmail(SendEmailVerificationRequest request);

    VerifyEmailResponse verify(String email, String code);

    void verifyToken(String token, String email);
}
