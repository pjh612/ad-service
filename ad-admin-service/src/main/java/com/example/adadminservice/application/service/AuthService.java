package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.AuthUseCase;
import com.example.adadminservice.application.in.dto.LoginResponse;
import com.example.adadminservice.application.in.dto.RefreshTokenResponse;
import com.example.adadminservice.application.out.cache.CacheProvider;
import com.example.adadminservice.application.out.token.TokenProvider;
import com.example.adadminservice.domain.model.Admin;
import com.example.adadminservice.domain.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CacheProvider cacheProvider;

    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 15 * 60;
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 2 * 60 * 60;

    @Override
    public LoginResponse login(String email, String password) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("어드민 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Map<String, Object> claims = Map.of("id", admin.getId());

        String accessToken = tokenProvider.generateToken(admin.getId().toString(), claims, ACCESS_TOKEN_EXPIRATION_SECONDS * 1000);
        String refreshToken = tokenProvider.generateToken(admin.getId().toString(), null, REFRESH_TOKEN_EXPIRATION_SECONDS * 1000);

        cacheProvider.save(refreshToken, accessToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS));

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken) || isBlocked(refreshToken)) {
            throw new RuntimeException("올바른 토큰이 아닙니다.");
        }

        String newAccessToken = tokenProvider.renewToken(refreshToken, ACCESS_TOKEN_EXPIRATION_SECONDS * 1000);
        String newRefreshToken = tokenProvider.renewToken(refreshToken, REFRESH_TOKEN_EXPIRATION_SECONDS * 1000);

        cacheProvider.save(String.format("blocked:token:%s", refreshToken), null, Duration.ofSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS));

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    private boolean isBlocked(String refreshToken) {
        return cacheProvider.hasKey(String.format("blocked:token:%s", refreshToken));
    }
}
