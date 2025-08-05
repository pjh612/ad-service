package com.example.adservice.infrastructure.web.controller;

import com.example.adservice.application.in.BusinessVerificationUseCase;
import com.example.adservice.application.in.dto.VerifyBusinessNumberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business-number")
public class BusinessNumberVerificationController {
    private final BusinessVerificationUseCase businessVerificationUseCase;

    @PostMapping
    public VerifyBusinessNumberResponse verifyBusinessNumber(
            @RequestParam String businessNumber,
            @RequestParam String representativeName,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startAt
    ) {
        return businessVerificationUseCase.verify(businessNumber, representativeName, startAt);
    }
}
