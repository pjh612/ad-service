package com.example.adservice.application.service;

import com.example.adservice.application.in.dto.VerifyBusinessNumberResponse;
import com.example.adservice.application.out.ExternalBusinessInfoApiClientPort;
import com.example.adservice.application.out.cache.CacheProvider;
import com.example.adservice.application.out.dto.BusinessInfo;
import com.example.adservice.domain.vo.BusinessNumberVerificationInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class BusinessVerificationServiceTest {
    private static final String BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX = "biz:verification:success:";
    @Mock
    private ExternalBusinessInfoApiClientPort externalBusinessInfoApiPort;
    @Mock
    private CacheProvider cacheProvider;
    @InjectMocks
    private BusinessVerificationService service;

    @Test
    void verify_ValidBusinessInfo_ReturnsToken() {
        // Given
        String businessNumber = "1234567890";
        String repName = "홍길동";
        LocalDate startAt = LocalDate.now();
        BusinessInfo validBusinessInfo = new BusinessInfo(true, "01", "개인사업자");
        given(externalBusinessInfoApiPort.getBusinessInfo(businessNumber, repName, startAt))
                .willReturn(validBusinessInfo);
        doNothing().when(cacheProvider).save(ArgumentMatchers.any(), ArgumentMatchers.any(BusinessNumberVerificationInfo.class), ArgumentMatchers.any(Duration.class));

        // When
        VerifyBusinessNumberResponse response = service.verify(businessNumber, repName, startAt);

        // Then
        assertNotNull(response);
        assertNotNull(response.token());
        // 토큰 형식: UUID
        assertTrue(response.token().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    void verify_InvalidBusinessInfo_ThrowsException() {
        // Given
        String businessNumber = "1234567890";
        String repName = "홍길동";
        LocalDate startAt = LocalDate.now();
        BusinessInfo validBusinessInfo = new BusinessInfo(false, null, null);
        given(externalBusinessInfoApiPort.getBusinessInfo(businessNumber, repName, startAt))
                .willReturn(validBusinessInfo); // isValid() false

        // When & Then
        assertThatThrownBy(() -> service.verify(businessNumber, repName, startAt))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("유효하지 않은 사업자 번호입니다.");
    }

    @Test
    void verifyToken_ValidTokenAndMatchingInfo_DoesNotThrow() {
        // Given
        String token = "valid-token";
        String businessNumber = "1234567890";
        String repName = "홍길동";
        LocalDate startAt = LocalDate.now();
        BusinessNumberVerificationInfo cachedInfo = new BusinessNumberVerificationInfo(businessNumber, repName, startAt);
        given(cacheProvider.get(BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX + token, BusinessNumberVerificationInfo.class))
                .willReturn(Optional.of(cachedInfo));
        // When & Then
        assertDoesNotThrow(() ->
                service.verifyToken(token, businessNumber, repName, startAt));
    }

    @Test
    void verifyToken_ExpiredToken_ThrowsException() {
        // Given
        String token = "expired-token";
        given(cacheProvider.get(BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX + token, BusinessNumberVerificationInfo.class))
                .willReturn(Optional.empty());
        // When & Then
        assertThatThrownBy(() -> service.verifyToken(token, "1234567890", "홍길동", LocalDate.now()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("인증 토큰이 만료되었습니다.");
    }
}