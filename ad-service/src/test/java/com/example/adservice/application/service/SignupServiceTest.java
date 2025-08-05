package com.example.adservice.application.service;

import com.example.adservice.application.in.BusinessVerificationUseCase;
import com.example.adservice.application.in.EmailVerificationUseCase;
import com.example.adservice.application.in.dto.SignupRequest;
import com.example.adservice.application.in.dto.SignupResponse;
import com.example.adservice.domain.model.Advertiser;
import com.example.adservice.domain.model.AuditInfo;
import com.example.adservice.domain.repository.AdvertiserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {
    @Mock
    private BusinessVerificationUseCase businessVerificationUseCase;
    @Mock
    private EmailVerificationUseCase emailVerificationUseCase;
    @Mock
    private AdvertiserRepository advertiserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private SignupService signupService;
    private final SignupRequest validRequest = new SignupRequest(
            "user123",
            "password123",
            "홍길동",
            "test@example.com",
            "123-45-67890", // 사업자번호 (하이픈 포함)
            LocalDate.of(2020, 1, 1),
            "businessToken123",
            "emailToken456"
    );
    @Test
    void signup_success() {
        // Given
        String formattedBusinessNumber = "1234567890";
        String encodedPassword = "encodedPassword123";
        UUID advertiserId = UUID.randomUUID();
        Advertiser savedAdvertiser = new Advertiser(advertiserId, "user123", encodedPassword, "홍길동", "test@example.com", formattedBusinessNumber, LocalDate.of(2020, 1, 1), AuditInfo.create(advertiserId.toString()));
        // Mocking
        given(passwordEncoder.encode(validRequest.password())).willReturn(encodedPassword);
        given(advertiserRepository.save(any(Advertiser.class))).willReturn(savedAdvertiser);
        // When
        SignupResponse response = signupService.signup(validRequest);
        // Then
        verify(businessVerificationUseCase).verifyToken(
                validRequest.businessNumberVerificationToken(),
                formattedBusinessNumber,
                validRequest.name(),
                validRequest.startAt()
        );
        verify(emailVerificationUseCase).verifyToken(
                validRequest.emailVerificationToken(),
                validRequest.email()
        );
        verify(passwordEncoder).encode(validRequest.password());
        verify(advertiserRepository).save(any(Advertiser.class));
        assertEquals(savedAdvertiser.getId(), response.id());
    }

    @Test
    void signup_businessVerificationFailure() {
        // Given
        doThrow(new RuntimeException("Business verification failed"))
                .when(businessVerificationUseCase).verifyToken(anyString(), anyString(), anyString(), any(LocalDate.class));
        // When & Then
        assertThrows(RuntimeException.class, () -> signupService.signup(validRequest));
        verifyNoInteractions(emailVerificationUseCase, advertiserRepository);
    }

    @Test
    void signup_emailVerificationFailure() {
        // Given
        doThrow(new RuntimeException("Email verification failed"))
                .when(emailVerificationUseCase).verifyToken(anyString(), anyString());
        // When & Then
        assertThrows(RuntimeException.class, () -> signupService.signup(validRequest));
        verify(businessVerificationUseCase).verifyToken(anyString(), anyString(), anyString(), any(LocalDate.class));
        verifyNoInteractions(advertiserRepository);
    }
    @Test
    void signup_passwordEncoding() {
        // Given
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(validRequest.password())).thenReturn(encodedPassword);
        when(advertiserRepository.save(any(Advertiser.class))).thenAnswer(invocation -> {
            Advertiser advertiser = invocation.getArgument(0);
            // 저장되는 Advertiser의 비밀번호가 인코딩된 것인지 확인
            assertEquals(encodedPassword, advertiser.getPassword());
            return advertiser;
        });
        // When
        signupService.signup(validRequest);
        // Then
        verify(passwordEncoder).encode(validRequest.password());
    }
    @Test
    void signup_repositoryFailure() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doThrow(new RuntimeException("DB save failed"))
                .when(advertiserRepository).save(any(Advertiser.class));
        // When & Then
        assertThrows(RuntimeException.class, () -> signupService.signup(validRequest));
    }
}