package com.example.adservice.infrastructure.web.controller;

import com.example.adservice.application.in.SignupUseCase;
import com.example.adservice.application.in.dto.SignupRequest;
import com.example.adservice.application.in.dto.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/advertisers")
public class SignupController {

    private final SignupUseCase signupUseCase;

    @PostMapping
    public SignupResponse signUp(@RequestBody SignupRequest request) {
        return signupUseCase.signup(request);
    }
}
