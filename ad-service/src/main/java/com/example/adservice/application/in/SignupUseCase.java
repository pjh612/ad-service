package com.example.adservice.application.in;

import com.example.adservice.application.in.dto.SignupRequest;
import com.example.adservice.application.in.dto.SignupResponse;

public interface SignupUseCase {
    SignupResponse signup(SignupRequest request);
}
