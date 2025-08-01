package com.example.adadminservice.infrastructure.security.config;

import com.example.adadminservice.application.out.token.TokenProvider;
import com.example.adadminservice.infrastructure.security.jwt.JjwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public TokenProvider jwtTokenProvider(@Value("${secret}") String secret) {
        return new JjwtProvider(secret);
    }
}
