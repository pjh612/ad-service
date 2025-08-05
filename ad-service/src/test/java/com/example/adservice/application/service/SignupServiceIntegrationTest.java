package com.example.adservice.application.service;

import com.example.adservice.TestClockConfiguration;
import com.example.adservice.TestRedisConfiguration;
import com.example.adservice.application.in.BusinessVerificationUseCase;
import com.example.adservice.application.in.EmailVerificationUseCase;
import com.example.adservice.application.in.SignupUseCase;
import com.example.adservice.application.in.dto.SignupRequest;
import com.example.adservice.application.in.dto.SignupResponse;
import com.example.adservice.domain.model.Advertiser;
import com.example.adservice.domain.repository.AdvertiserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import({TestRedisConfiguration.class, TestClockConfiguration.class})
public class SignupServiceIntegrationTest {
    @Autowired
    private SignupUseCase signupService;
    @MockitoBean
    private BusinessVerificationUseCase businessVerificationUseCase;
    @MockitoBean
    private EmailVerificationUseCase emailVerificationUseCase;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    void signup_success() {
        // Given
        SignupRequest request = new SignupRequest(
                "test",
                "password",
                "name",
                "test@test.com",
                "1234567890",
                LocalDate.now(),
                "mockToken",
                "mockToken2"
        );
        // 인증 토큰 검증 성공하도록 Mocking
        doNothing().when(businessVerificationUseCase).verifyToken(anyString(), anyString(), anyString(), any(LocalDate.class));
        doNothing().when(emailVerificationUseCase).verifyToken(anyString(), anyString());
        // When
        SignupResponse response = signupService.signup(request);
        // Then
        Advertiser saved = advertiserRepository.findById(response.id()).orElseThrow();
        Assertions.assertThat(saved.getUsername()).isEqualTo(request.id());
        Assertions.assertThat(passwordEncoder.matches(request.password(), saved.getPassword())).isTrue();
    }

    @Test
    @Transactional
    void signup_fail_if_email_verification_token_is_invalid() {
        // Given
        SignupRequest request = new SignupRequest(
                "test",
                "password",
                "name",
                "test@test.com",
                "1234567890",
                LocalDate.now(),
                "mockToken",
                "mockToken2"
        );
        // 인증 토큰 검증 성공하도록 Mocking
        doThrow(RuntimeException.class).when(emailVerificationUseCase).verifyToken(anyString(), anyString());
        doNothing().when(businessVerificationUseCase).verifyToken(anyString(), anyString(), anyString(), any(LocalDate.class));
        // When & Then
        Assertions.assertThatThrownBy(()->signupService.signup(request)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @Transactional
    void signup_fail_if_business_number_verification_token_is_invalid() {
        // Given
        SignupRequest request = new SignupRequest(
                "test",
                "password",
                "name",
                "test@test.com",
                "1234567890",
                LocalDate.now(),
                "mockToken",
                "mockToken2"
        );
        // 인증 토큰 검증 성공하도록 Mocking
        doThrow(RuntimeException.class).when(businessVerificationUseCase).verifyToken(anyString(), anyString(), anyString(), any(LocalDate.class));
        doNothing().when(emailVerificationUseCase).verifyToken(anyString(), anyString());
        // When & Then
        Assertions.assertThatThrownBy(()->signupService.signup(request)).isInstanceOf(RuntimeException.class);
    }
}
