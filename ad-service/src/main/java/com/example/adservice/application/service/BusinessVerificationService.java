package com.example.adservice.application.service;

import com.example.adservice.application.in.BusinessVerificationUseCase;
import com.example.adservice.application.in.dto.VerifyBusinessNumberResponse;
import com.example.adservice.application.out.ExternalBusinessInfoApiClientPort;
import com.example.adservice.application.out.cache.CacheProvider;
import com.example.adservice.application.out.dto.BusinessInfo;
import com.example.adservice.domain.vo.BusinessNumberVerificationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessVerificationService implements BusinessVerificationUseCase {
    private final ExternalBusinessInfoApiClientPort externalBusinessInfoApiPort;
    private final CacheProvider cacheProvider;

    private static final String BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX = "biz:verification:success:";
    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    @Override
    public VerifyBusinessNumberResponse verify(String businessNumber, String representativeName, LocalDate startAt) {
        businessNumber = BusinessNumberVerificationInfo.formatBusinessNumber(businessNumber);
        BusinessInfo businessInfo = externalBusinessInfoApiPort.getBusinessInfo(businessNumber, representativeName, startAt);

        if (businessInfo == null || !businessInfo.isValid()) {
            throw new RuntimeException("유효하지 않은 사업자 번호입니다.");
        }

        String successKey = UUID.randomUUID().toString();
        BusinessNumberVerificationInfo businessNumberVerification = new BusinessNumberVerificationInfo(businessNumber, representativeName, startAt);
        cacheProvider.save(BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX + successKey, businessNumberVerification, Duration.ofMinutes(TOKEN_EXPIRATION_MINUTES));

        return new VerifyBusinessNumberResponse(successKey);
    }

    @Override
    public void verifyToken(String token, String businessNumber, String representativeName, LocalDate startAt) {
        businessNumber = BusinessNumberVerificationInfo.formatBusinessNumber(businessNumber);
        BusinessNumberVerificationInfo businessNumberVerification = new BusinessNumberVerificationInfo(businessNumber, representativeName, startAt);
        BusinessNumberVerificationInfo cachedVerification = cacheProvider.get(BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX + token, BusinessNumberVerificationInfo.class)
                .orElseThrow(() -> new RuntimeException("인증 토큰이 만료되었습니다."));

        if (!businessNumberVerification.equals(cachedVerification)) {
            throw new RuntimeException("인증 유효성 검사에 실패 했습니다.");
        }
    }
}
