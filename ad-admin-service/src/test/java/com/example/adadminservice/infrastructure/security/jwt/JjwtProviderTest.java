package com.example.adadminservice.infrastructure.security.jwt;

import com.example.adadminservice.application.out.token.TokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class JjwtProviderTest {
    TokenProvider tokenProvider = new JjwtProvider("a".repeat(64));

    @Test
    void validateToken_should_return_true_for_valid_token() {
        String token = tokenProvider.generateToken("user-id", Map.of(), 1000 * 60); // 1분
        boolean valid = tokenProvider.validateToken(token);
        Assertions.assertThat(valid).isTrue();
    }

    @Test
    void validateToken_should_return_false_for_expired_token() throws InterruptedException {
        String token = tokenProvider.generateToken("user-id", Map.of(), 1000); // 1초
        Thread.sleep(1500); // 1.5초 대기
        boolean valid = tokenProvider.validateToken(token);
        Assertions.assertThat(valid).isFalse();
    }

    @Test
    void validateToken_should_return_false_for_token_with_invalid_signature() {
        String validToken = tokenProvider.generateToken("user-id", Map.of(), 60000);

        // 잘못된 키로 만든 Provider (서명 검증 실패)
        JjwtProvider invalidProvider = new JjwtProvider("b".repeat(64));
        boolean isValid = invalidProvider.validateToken(validToken);

        Assertions.assertThat(isValid).isFalse();
    }

}