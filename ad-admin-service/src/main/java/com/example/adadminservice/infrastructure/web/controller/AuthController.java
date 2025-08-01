package com.example.adadminservice.infrastructure.web.controller;

import com.example.adadminservice.application.in.AuthUseCase;
import com.example.adadminservice.application.in.dto.LoginRequest;
import com.example.adadminservice.application.in.dto.LoginResponse;
import com.example.adadminservice.application.in.dto.RefreshTokenResponse;
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
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthUseCase authUseCase;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authUseCase.login(request.email(), request.password());
        response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(loginResponse.refreshToken()).toString());

        return ResponseEntity.ok(Map.of("accessToken", loginResponse.accessToken()));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken, HttpServletResponse response) {
        try {
            RefreshTokenResponse tokens = authUseCase.refreshToken(refreshToken);
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
