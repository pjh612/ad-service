package com.example.adservice.application.out;

import com.example.adservice.application.out.dto.BusinessInfo;

import java.time.LocalDate;

public interface ExternalBusinessInfoApiClientPort {
    BusinessInfo getBusinessInfo(String businessNumber, String representativeName, LocalDate startAt);
}
