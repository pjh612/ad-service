package com.example.adservice.infrastructure.web.controller;

import com.example.adservice.application.in.EmailVerificationUseCase;
import com.example.adservice.application.in.dto.VerifyEmailResponse;
import com.example.adservice.application.in.dto.SendEmailVerificationRequest;
import com.example.adservice.application.in.dto.VerifyEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {
    private final EmailVerificationUseCase emailVerificationUseCase;

    @PostMapping("/send-verification")
    public void sendEmail(@RequestBody SendEmailVerificationRequest request) {
        emailVerificationUseCase.sendEmail(request);
    }

    @PostMapping("/verify")
    public VerifyEmailResponse verify(@RequestBody VerifyEmailRequest request) {
       return emailVerificationUseCase.verify(request.email(), request.code());
    }
}
