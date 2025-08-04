package com.example.adservice.application.service;

import com.example.adservice.TestClockConfiguration;
import com.example.adservice.TestRedisConfiguration;
import com.example.adservice.application.in.dto.SendEmailVerificationRequest;
import com.example.adservice.application.in.dto.VerifyEmailResponse;
import com.example.adservice.application.out.cache.CacheProvider;
import com.example.adservice.application.out.mail.MailSender;
import com.example.adservice.domain.vo.MailVerificationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestRedisConfiguration.class, TestClockConfiguration.class})
class EmailVerificationServiceIntegrationTest {

    @Autowired
    EmailVerificationService emailVerificationService;

    @Autowired
    CacheProvider cacheProvider;

    @MockitoBean
    MailSender mailSender;

    private final SendEmailVerificationRequest request = new SendEmailVerificationRequest("test@test.com");
    private final String key = "email:verification:code:" + request.email();

    @BeforeEach
    void setUp() {
        doNothing().when(mailSender).send(any(), any(),any());
        doNothing().when(mailSender).send(any(), any(),any(), any());
        cacheProvider.delete(key);
    }

    @Test
    void sendEmailTest() {
        // When
        emailVerificationService.sendEmail(request);

        // Then
        MailVerificationCode code = cacheProvider.get(key, MailVerificationCode.class).get();
        cacheProvider.delete(key);

        assertThat(code.getCode()).matches("\\d{6}"); // 6자리 숫자 확인
        assertThat(code).isNotNull();
        assertThat(code.getCode()).isNotNull();
        assertThat(code.getFailCount()).isEqualTo(0);
        assertThat(code.getSendCount()).isEqualTo(1);
    }

    @Test
    void resendEmailTest() {
        // When
        emailVerificationService.sendEmail(request);
        MailVerificationCode code = cacheProvider.get(key, MailVerificationCode.class).get();
        emailVerificationService.sendEmail(request);


        // Then
        MailVerificationCode resendCode = cacheProvider.get(key, MailVerificationCode.class).get();
        cacheProvider.delete(key);

        assertThat(resendCode.getCode()).isNotEqualTo(code.getCode());
        assertThat(resendCode).isNotNull();
        assertThat(resendCode.getCode()).isNotNull();
        assertThat(resendCode.getFailCount()).isEqualTo(0);
        assertThat(resendCode.getSendCount()).isEqualTo(2);
    }

    @Test
    void verifyEmailTest() {
        // Given
        emailVerificationService.sendEmail(request);
        MailVerificationCode code = cacheProvider.get(key, MailVerificationCode.class).get();

        // When
        VerifyEmailResponse response = emailVerificationService.verify(request.email(), code.getCode());

        // Then
        String successKey = "email:verification:success:" + response.token();

        assertThat(response.token()).isNotNull();
        assertThat(cacheProvider.hasKey(key)).isFalse();
        assertThat(cacheProvider.hasKey(successKey)).isTrue();

        cacheProvider.delete(key);
        cacheProvider.delete(successKey);
    }

    @Test
    void verifyEmailFailTest() {
        // When & Then
        emailVerificationService.sendEmail(request);

        assertThatThrownBy(() -> emailVerificationService.verify(request.email(), "code"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("인증 코드가 일치하지 않습니다.");;
        cacheProvider.delete(key);
    }

    @Test
    void resendLimitExceededTest() {
        // 3회 전송
        IntStream.range(0, 3).forEach(i -> emailVerificationService.sendEmail(request));

        // When & Then (4번째 시도)
        assertThatThrownBy(() -> emailVerificationService.sendEmail(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("인증 메일 재전송 제한 횟수를 초과했습니다.");

        cacheProvider.delete(key);
    }

}