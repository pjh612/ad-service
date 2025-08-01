package com.example.adadminservice.common;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomPasswordGenerator {
    private static final RandomStringGenerator GENERATOR = new RandomStringGenerator.Builder()
            .withinRange('0', 'z') // 범위 지정
            .filteredBy(Character::isLetterOrDigit) // 문자/숫자만 필터링
            .usingRandom(new SecureRandom()::nextInt)
            .build();

    public String generate(int length) {
        return GENERATOR.generate(length);
    }
}
