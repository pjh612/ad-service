package com.example.adservice.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean("externalBusinessInfoApiClient")
    RestClient externalBusinessInfoApiClient(RestClient.Builder builder, @Value("${client.business.url}") String url) {
        return builder.baseUrl(url).build();
    }
}
