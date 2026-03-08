package com.internalemployeeportal.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BackgroundCheckClientConfig {

    private final BackgroundCheckProperties properties;

    @Bean
    public RestClient backgroundCheckRestClient() {
        log.info("Initializing BackgroundCheckClient with base URL: {}", properties.getBaseUrl());
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}