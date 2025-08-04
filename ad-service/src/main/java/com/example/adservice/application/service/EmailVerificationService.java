package com.example.adservice.application.service;

import com.example.adservice.application.in.EmailVerificationUseCase;
import com.example.adservice.domain.vo.MailVerificationCode;
import com.example.adservice.application.in.dto.SendEmailVerificationRequest;
import com.example.adservice.application.in.dto.VerifyEmailResponse;
import com.example.adservice.application.out.cache.CacheProvider;
import com.example.adservice.application.out.mail.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService implements EmailVerificationUseCase {
    private final MailSender mailSender;
    private final CacheProvider cacheProvider;
    private final Clock clock;

    private static final String EMAIL_VERIFICATION_CODE_CACHE_KEY_PREFIX = "email:verification:code:";
    private static final String EMAIL_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX = "email:verification:success:";
    private static final int EMAIL_RESEND_BLOCK_TIME_MINUTES = 30;
    private static final int EMAIL_VERIFICATION_CODE_VALID_MINUTES = 3;
    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    @Override
    public void sendEmail(SendEmailVerificationRequest request) {
        Instant now = Instant.now(clock);
        String email = request.email();
        String key = getCodeCacheKey(email);
        String code = generateRandomCode();
        Instant expireAt = now.plus(Duration.ofMinutes(EMAIL_VERIFICATION_CODE_VALID_MINUTES));
        MailVerificationCode mailVerificationCode = cacheProvider.get(key, MailVerificationCode.class)
                .map(it -> it.renewForResend(code, expireAt))
                .orElse(MailVerificationCode.create(code, expireAt));

        if (mailVerificationCode.getSendCount() > 3) {
            throw new IllegalArgumentException(String.format("인증 메일 재전송 제한 횟수를 초과했습니다. %d분 후 다시 시도해주세요.", EMAIL_RESEND_BLOCK_TIME_MINUTES));
        }

        cacheProvider.save(key, mailVerificationCode, Duration.ofMinutes(EMAIL_RESEND_BLOCK_TIME_MINUTES));

        Map<String, Object> templateData = Map.of(
                "code", mailVerificationCode.getCode(),
                "expireMinutes", 3
        );
        mailSender.send(email, "[광고 서비스] 이메일 인증 코드", templateData, "email-verification");
    }

    @Override
    public VerifyEmailResponse verify(String email, String code) {
        Instant now = Instant.now(clock);
        String key = getCodeCacheKey(email);
        MailVerificationCode mailVerificationCode = cacheProvider.get(key, MailVerificationCode.class)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 유효하지 않습니다."));
        if (mailVerificationCode.isExpired(now)) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다. 재발급 해주세요.");
        }

        if (!mailVerificationCode.verify(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        cacheProvider.delete(key);

        String successKey = UUID.randomUUID().toString();
        cacheProvider.save(EMAIL_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX + successKey, email, Duration.ofMinutes(TOKEN_EXPIRATION_MINUTES));

        return new VerifyEmailResponse(successKey);
    }

    @Override
    public void verifyToken(String token, String email) {
        String cachedEmail = cacheProvider.get(EMAIL_VERIFICATION_SUCCESS_CACHE_KEY_PREFIX + token, String.class)
                .orElseThrow(() -> new RuntimeException("인증 토큰이 만료되었습니다."));

        if (!cachedEmail.equals(email)) {
            throw new RuntimeException("인증 유효성 검사에 실패 했습니다.");
        }
    }

    private String getCodeCacheKey(String email) {
        return EMAIL_VERIFICATION_CODE_CACHE_KEY_PREFIX + email;
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(999999));
    }
}
