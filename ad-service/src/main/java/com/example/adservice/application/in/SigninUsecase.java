package com.example.adservice.application.in;

import com.example.adservice.application.in.dto.RefreshTokenResponse;
import com.example.adservice.application.in.dto.SigninRequest;
import com.example.adservice.application.in.dto.SigninResponse;

public interface SigninUsecase {
    SigninResponse signin(SigninRequest request);

    RefreshTokenResponse refreshToken(String refreshToken);
}
