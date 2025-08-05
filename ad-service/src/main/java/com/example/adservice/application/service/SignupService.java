package com.example.adservice.application.service;

import com.example.adservice.application.in.BusinessVerificationUseCase;
import com.example.adservice.application.in.EmailVerificationUseCase;
import com.example.adservice.application.in.SignupUseCase;
import com.example.adservice.application.in.dto.SignupRequest;
import com.example.adservice.application.in.dto.SignupResponse;
import com.example.adservice.domain.model.Advertiser;
import com.example.adservice.domain.repository.AdvertiserRepository;
import com.example.adservice.domain.vo.BusinessNumberVerificationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService implements SignupUseCase {
    private final BusinessVerificationUseCase businessVerificationUseCase;
    private final EmailVerificationUseCase emailVerificationUseCase;
    private final AdvertiserRepository advertiserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String formattedBusinessNumber = BusinessNumberVerificationInfo.formatBusinessNumber(request.businessNumber());
        businessVerificationUseCase.verifyToken(request.businessNumberVerificationToken(), formattedBusinessNumber, request.name(), request.startAt());
        emailVerificationUseCase.verifyToken(request.emailVerificationToken(), request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        Advertiser advertiser = Advertiser.create(request.id(), encodedPassword, request.name(), request.email(), formattedBusinessNumber, request.startAt());
        Advertiser saved = advertiserRepository.save(advertiser);

        return new SignupResponse(saved.getId());
    }
}
