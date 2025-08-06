package com.example.adservice.infrastructure.web.controller;

import com.example.adservice.application.in.SigninUsecase;
import com.example.adservice.application.in.dto.RefreshTokenResponse;
import com.example.adservice.application.in.dto.SigninRequest;
import com.example.adservice.application.in.dto.SigninResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/advertiser/auth")
public class SigninController {
    private final SigninUsecase signinUsecase;

    @PostMapping
    public ResponseEntity<?> signin(@RequestBody SigninRequest request, HttpServletResponse response) {
        SigninResponse signinResponse = signinUsecase.signin(request);
        response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(signinResponse.refreshToken()).toString());

        return ResponseEntity.ok(Map.of("accessToken", signinResponse.accessToken()));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        try {
            RefreshTokenResponse tokens = signinUsecase.refreshToken(refreshToken);
            response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(tokens.refreshToken()).toString());

            return ResponseEntity.ok(Map.of("accessToken", tokens.accessToken()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or revoked Token");
        }
    }

    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}
