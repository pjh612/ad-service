package com.example.adservice.domain.vo;

import java.time.LocalDate;

public record BusinessNumberVerificationInfo(String businessNumber, String representativeName, LocalDate startAt) {
    public static String formatBusinessNumber(String businessNumber) {
        return businessNumber.trim().replace("-", "");
    }
}
