package com.example.adservice.domain.model;

import com.example.adservice.domain.vo.MailVerificationCode;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MailVerificationCodeTest {
    @Test
    void create_firstVerificationCode() {
        // Given
        String code = "123456";
        Instant now = Instant.now();
        Instant expireAt = now.plus(Duration.ofMinutes(3));

        // When
        MailVerificationCode verificationCode = MailVerificationCode.create(code, expireAt);

        // Then
        assertThat(verificationCode.getCode()).isEqualTo(code);
        assertThat(verificationCode.getSendCount()).isEqualTo(1); // 초기 전송 시 1
        assertThat(verificationCode.getFailCount()).isZero();
        assertThat(verificationCode.getExpireAt()).isEqualTo(expireAt);
    }

    @Test
    void renewForResend_updatesCodeAndIncrementSendCount() {
        // Given
        MailVerificationCode original = MailVerificationCode.create("111111", Instant.now().plus(3, ChronoUnit.MINUTES));
        String newCode = "654321";
        Instant newExpireAt = Instant.now().plus(5, ChronoUnit.MINUTES);

        // When
        MailVerificationCode renewed = original.renewForResend(newCode, newExpireAt);

        // Then
        assertThat(renewed.getCode()).isEqualTo(newCode);
        assertThat(renewed.getSendCount()).isEqualTo(2); // 재전송 시 +1
        assertThat(renewed.getFailCount()).isZero(); // 실패 횟수 초기화
        assertThat(renewed.getExpireAt()).isEqualTo(newExpireAt);
    }

    @Test
    void verify_success() {
        MailVerificationCode code = MailVerificationCode.create("123456", Instant.now().plus(3, ChronoUnit.MINUTES));
        assertThat(code.verify("123456")).isTrue();
        assertThat(code.getFailCount()).isZero(); // 실패 횟수 증가 X
    }

    @Test
    void verify_failure_incrementsFailCount() {
        MailVerificationCode code = MailVerificationCode.create("123456", Instant.now().plus(3, ChronoUnit.MINUTES));
        assertThat(code.verify("wrong")).isFalse();
        assertThat(code.getFailCount()).isEqualTo(1); // 실패 횟수 +1
    }

    @Test
    void isExpired_whenNotExpired_returnsFalse() {
        Instant now = Instant.now();
        MailVerificationCode code = MailVerificationCode.create("123456", Instant.now().plus(3, ChronoUnit.MINUTES));
        assertThat(code.isExpired(now)).isFalse();
    }

    @Test
    void isExpired_whenExpired_returnsTrue() {
        Instant now = Instant.now();
        MailVerificationCode code = MailVerificationCode.create("123456", now.minusSeconds(1)); // 이미 만료
        assertThat(code.isExpired(now)).isTrue();
    }
}