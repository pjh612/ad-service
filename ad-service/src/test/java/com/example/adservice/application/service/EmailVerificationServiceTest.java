package com.example.adservice.application.service;

import com.example.adservice.application.in.dto.SendEmailVerificationRequest;
import com.example.adservice.application.in.dto.VerifyEmailResponse;
import com.example.adservice.application.out.cache.CacheProvider;
import com.example.adservice.application.out.mail.MailSender;
import com.example.adservice.domain.vo.MailVerificationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {
    @Mock
    private CacheProvider cacheProvider;
    @Mock
    private MailSender mailSender;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private final Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    @BeforeEach
    void setUp() {
        emailVerificationService = new EmailVerificationService(
                mailSender,
                cacheProvider,
                fixedClock
        );
    }

    @Test
    void sendEmail_firstTime_savesNewCode() {
        // Given
        String email = "test@example.com";
        given(cacheProvider.get(any(), any())).willReturn(Optional.empty());

        // When
        emailVerificationService.sendEmail(new SendEmailVerificationRequest(email));

        // Then
        ArgumentCaptor<MailVerificationCode> captor = ArgumentCaptor.forClass(MailVerificationCode.class);
        verify(cacheProvider).save(eq("email:verification:code:" + email), captor.capture(), any());

        MailVerificationCode savedCode = captor.getValue();
        assertThat(savedCode.getCode()).hasSize(6);
        assertThat(savedCode.getSendCount()).isEqualTo(1);
        verify(mailSender).send(eq(email), any(), any(), any());
    }

    @Test
    void sendEmail_resend_updatesExistingCode() {
        // Given
        String email = "test@example.com";
        MailVerificationCode existing = MailVerificationCode.create("111111", Instant.now().plus(3, ChronoUnit.MINUTES));
        given(cacheProvider.get(any(), any())).willReturn(Optional.of(existing));

        // When
        emailVerificationService.sendEmail(new SendEmailVerificationRequest(email));

        // Then
        ArgumentCaptor<MailVerificationCode> captor = ArgumentCaptor.forClass(MailVerificationCode.class);
        verify(cacheProvider).save(any(), captor.capture(), any());

        MailVerificationCode updatedCode = captor.getValue();
        assertThat(updatedCode.getSendCount()).isEqualTo(2); // 재전송 횟수 증가
        assertThat(updatedCode.getFailCount()).isZero(); // 실패 횟수 초기화
    }

    @Test
    void sendEmail_resendLimitExceeded_throwsException() {
        // Given
        MailVerificationCode code = MailVerificationCode.create("111111", Instant.now().plus(3, ChronoUnit.MINUTES));
        code = code.renewForResend("222222", Instant.now().plus(3, ChronoUnit.MINUTES)); // sendCount = 2
        code = code.renewForResend("333333", Instant.now().plus(3, ChronoUnit.MINUTES)); // sendCount = 3
        given(cacheProvider.get(any(), any())).willReturn(Optional.of(code));

        // When & Then
        assertThatThrownBy(() -> emailVerificationService.sendEmail(new SendEmailVerificationRequest("test@example.com")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재전송 제한 횟수를 초과");
    }

    @Test
    void verify_validCode_deletesCode() {
        // Given
        String email = "test@example.com";
        MailVerificationCode code = MailVerificationCode.create("123456", Instant.now().plus(3, ChronoUnit.MINUTES));
        given(cacheProvider.get(any(), any())).willReturn(Optional.of(code));

        // When
        VerifyEmailResponse response = emailVerificationService.verify(email, "123456");

        // Then
        verify(cacheProvider).delete("email:verification:code:" + email);
        verify(cacheProvider).save("email:verification:success:" + response.token(), email, Duration.ofMinutes(30));
    }

    @Test
    void verify_expiredCode_throwsException() {
        // Given
        MailVerificationCode code = MailVerificationCode.create("123456", Instant.now().minusSeconds(1)); // 만료됨
        given(cacheProvider.get(any(), any())).willReturn(Optional.of(code));

        // When & Then
        assertThatThrownBy(() -> emailVerificationService.verify("test@example.com", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("만료");
    }
}