package com.example.adadminservice.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {
    @Bean
    public Clock fixedClock() {
        return Clock.system(ZoneId.systemDefault());
    }
}
