package com.example.adservice.application.service;

import com.example.adservice.TestClockConfiguration;
import com.example.adservice.TestRedisConfiguration;
import com.example.adservice.application.in.dto.VerifyBusinessNumberResponse;
import com.example.adservice.application.out.ExternalBusinessInfoApiClientPort;
import com.example.adservice.application.out.cache.CacheProvider;
import com.example.adservice.application.out.dto.BusinessInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@Import({TestRedisConfiguration.class, TestClockConfiguration.class})
@SpringBootTest
public class BusinessVerificationServiceIntegrationTest {

    @Autowired
    BusinessVerificationService service;
    @Autowired
    private CacheProvider cacheProvider; // 실제 Redis 연동 구현체

    @MockitoBean
    private ExternalBusinessInfoApiClientPort externalBusinessInfoApiPort;

    private static final String BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX = "biz:verification:success:";

    @Test
    void verifyAndVerifyToken_IntegrationTest() {
        // Given
        String businessNumber = "1234567890";
        String representativeName = "홍길동";
        LocalDate startAt = LocalDate.now();
        BusinessInfo validBusinessInfo = new BusinessInfo(true, "01", "개인사업자");
        when(externalBusinessInfoApiPort.getBusinessInfo(businessNumber, representativeName, startAt))
                .thenReturn(validBusinessInfo);
        // When
        VerifyBusinessNumberResponse response = service.verify(businessNumber, representativeName, startAt);
        String token = response.token();

        // Then
        assertDoesNotThrow(() -> service.verifyToken(token, businessNumber, representativeName, startAt));
    }

    @Test
    void verifyToken_expired() {
        // Given
        String businessNumber = "1234567890";
        String representativeName = "홍길동";
        LocalDate startAt = LocalDate.now();
        BusinessInfo validBusinessInfo = new BusinessInfo(true, "01", "개인사업자");
        when(externalBusinessInfoApiPort.getBusinessInfo(businessNumber, representativeName, startAt))
                .thenReturn(validBusinessInfo);

        // When
        VerifyBusinessNumberResponse response = service.verify(businessNumber, representativeName, startAt);
        cacheProvider.delete(BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX + response.token());

        // Then
        assertThatThrownBy(() -> service.verifyToken(response.token(), businessNumber, representativeName, startAt))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("인증 토큰이 만료되었습니다.");
    }

    @Test
    void verifyToken_mismatch() {
        // Given
        String businessNumber = "1234567890";
        String representativeName = "홍길동";
        LocalDate startAt = LocalDate.now();
        BusinessInfo validBusinessInfo = new BusinessInfo(true, "01", "개인사업자");
        when(externalBusinessInfoApiPort.getBusinessInfo(businessNumber, representativeName, startAt))
                .thenReturn(validBusinessInfo);

        // When
        VerifyBusinessNumberResponse response = service.verify(businessNumber, representativeName, startAt);

        // Then
        assertThatThrownBy(() -> service.verifyToken(response.token(), "1234567891", representativeName, startAt))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("인증 유효성 검사에 실패 했습니다.");
        cacheProvider.delete(BUSINESS_NUMBER_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX + response.token());
    }
}
