package com.example.adadminservice.application.in;

import com.example.adadminservice.application.in.dto.LoginResponse;
import com.example.adadminservice.application.in.dto.RefreshTokenResponse;

public interface AuthUseCase {
    LoginResponse login(String email, String password);

    RefreshTokenResponse refreshToken(String refreshToken);
}
