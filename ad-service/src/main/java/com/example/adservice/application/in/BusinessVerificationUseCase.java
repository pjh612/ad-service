package com.example.adservice.application.in;

import com.example.adservice.application.in.dto.VerifyBusinessNumberResponse;

import java.time.LocalDate;

public interface BusinessVerificationUseCase {
    VerifyBusinessNumberResponse verify(String businessNumber, String representativeName, LocalDate startAt);

    void verifyToken(String token, String businessNumber, String representativeName, LocalDate startAt);
}
