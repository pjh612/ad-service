package com.example.adservice.application.service;

import com.example.adservice.application.exception.AuthException;
import com.example.adservice.application.in.SigninUsecase;
import com.example.adservice.application.in.dto.RefreshTokenResponse;
import com.example.adservice.application.in.dto.SigninRequest;
import com.example.adservice.application.in.dto.SigninResponse;
import com.example.adservice.application.out.cache.CacheProvider;
import com.example.adservice.application.out.token.TokenProvider;
import com.example.adservice.domain.model.Advertiser;
import com.example.adservice.domain.repository.AdvertiserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SigninService implements SigninUsecase {
    private final AdvertiserRepository advertiserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CacheProvider cacheProvider;

    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 15 * 60;
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 2 * 60 * 60;

    @Override
    public SigninResponse signin(SigninRequest request) {
        Advertiser advertiser = advertiserRepository.findByUsername(request.id())
                .orElseThrow(() -> new AuthException("아이디 비밀번호를 확인해 주세요."));

        if (!passwordEncoder.matches(request.password(), advertiser.getPassword())) {
            throw new AuthException("아이디 비밀번호를 확인해 주세요.");
        }


        Map<String, Object> claims = Map.of("username", advertiser.getUsername());
        String accessToken = tokenProvider.generateToken(advertiser.getId().toString(), claims, ACCESS_TOKEN_EXPIRATION_SECONDS * 1000);
        String refreshToken = tokenProvider.generateToken(advertiser.getId().toString(), null, ACCESS_TOKEN_EXPIRATION_SECONDS * 1000);

        cacheProvider.save(refreshToken, accessToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS));

        return new SigninResponse(accessToken, refreshToken);
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken) || isBlocked(refreshToken)) {
            throw new AuthException("올바른 토큰이 아닙니다.");
        }
        String accessToken = cacheProvider.get(refreshToken, String.class)
                .orElseThrow(() -> new AuthException("토큰이 유효하지 않습니다."));

        String newAccessToken = tokenProvider.renewToken(accessToken, ACCESS_TOKEN_EXPIRATION_SECONDS * 1000);
        String newRefreshToken = tokenProvider.renewToken(refreshToken, REFRESH_TOKEN_EXPIRATION_SECONDS * 1000);

        cacheProvider.save(String.format("blocked:token:%s", refreshToken), null, Duration.ofSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS));

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    private boolean isBlocked(String refreshToken) {
        return cacheProvider.hasKey(String.format("blocked:token:%s", refreshToken));
    }
}
