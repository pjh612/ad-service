package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.dto.LoginResponse;
import com.example.adadminservice.application.in.dto.RefreshTokenResponse;
import com.example.adadminservice.application.out.cache.CacheProvider;
import com.example.adadminservice.application.out.token.TokenProvider;
import com.example.adadminservice.domain.model.Admin;
import com.example.adadminservice.domain.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    TokenProvider tokenProvider;

    @Mock
    CacheProvider cacheProvider;

    @Mock
    AdminRepository adminRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    private final UUID adminId = UUID.randomUUID();
    private final String email = "admin@example.com";
    private final String password = "password";
    private final String encodedPassword = "encodedPassword";
    private final Admin admin = new Admin(
            adminId,
            email,
            encodedPassword,
            "First",
            "Last",
            "Dept",
            true,
            null,
            null,
            null);


    private static final String VALID_REFRESH_TOKEN = "valid.refresh.token";
    private static final long ACCESS_EXP = 15 * 60 * 1000;
    private static final long REFRESH_EXP = 2 * 60 * 60 * 1000;

    @Test
    void login_should_return_tokens_when_credentials_are_valid() {
        // given
        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(tokenProvider.generateToken(eq(adminId.toString()), anyMap(), anyLong())).willReturn("access-token");
        given(tokenProvider.generateToken(eq(adminId.toString()), isNull(), anyLong())).willReturn("refresh-token");

        // when
        LoginResponse result = authService.login(email, password);

        // then
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        then(cacheProvider).should().save(eq("refresh-token"), eq("access-token"), any());
    }

    @Test
    void login_should_throw_when_admin_not_found() {
        // given
        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("어드민 정보를 찾을 수 없습니다.");
    }

    @Test
    void login_should_throw_when_password_is_invalid() {
        // given
        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(email, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }


    @Test
    void refreshToken_should_return_new_tokens_when_valid() {
        given(tokenProvider.validateToken(VALID_REFRESH_TOKEN)).willReturn(true);
        given(cacheProvider.hasKey(String.format("blocked:token:%s", VALID_REFRESH_TOKEN))).willReturn(false);
        given(tokenProvider.renewToken(VALID_REFRESH_TOKEN, ACCESS_EXP)).willReturn("newAccessToken");
        given(tokenProvider.renewToken(VALID_REFRESH_TOKEN, REFRESH_EXP)).willReturn("newRefreshToken");

        RefreshTokenResponse response = authService.refreshToken(VALID_REFRESH_TOKEN);

        assertEquals("newAccessToken", response.accessToken());
        assertEquals("newRefreshToken", response.refreshToken());
    }

    @Test
    void refreshToken_should_throw_when_token_is_invalid() {
        given(tokenProvider.validateToken(VALID_REFRESH_TOKEN)).willReturn(false);

        assertThrows(RuntimeException.class, () -> authService.refreshToken(VALID_REFRESH_TOKEN));
    }

    @Test
    void refreshToken_should_throw_when_token_is_blocked() {
        given(tokenProvider.validateToken(VALID_REFRESH_TOKEN)).willReturn(true);
        given(cacheProvider.hasKey("blocked:token:" + VALID_REFRESH_TOKEN)).willReturn(true);

        assertThrows(RuntimeException.class, () -> authService.refreshToken(VALID_REFRESH_TOKEN));
    }
}
