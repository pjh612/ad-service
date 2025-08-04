package com.example.adservice.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailVerificationCode {
    private String code;
    private int failCount;
    private int sendCount;
    private Instant expireAt;

    public MailVerificationCode(String code, int failCount, int sendCount, Instant expireAt) {
        this.code = code;
        this.failCount = failCount;
        this.sendCount = sendCount;
        this.expireAt = expireAt;
    }

    public static MailVerificationCode create(String code, Instant expireAt) {
        return new MailVerificationCode(code, 0, 1, expireAt);
    }

    public boolean verify(String code) {
        boolean equals = this.code.equals(code);
        if (!equals) {
            this.failCount++;
        }
        return equals;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(this.expireAt);
    }

    public MailVerificationCode renewForResend(String newCode, Instant expireAt) {
        return new MailVerificationCode(newCode, 0, this.sendCount + 1, expireAt);
    }
}
