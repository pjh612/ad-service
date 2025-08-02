package com.example.adadminservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@TestConfiguration
public class TestClockConfiguration {


    @Bean
    public Clock fixedClock() {
        return Clock.fixed(LocalDateTime.of(2025,1,1,14,0,0).atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }
}
