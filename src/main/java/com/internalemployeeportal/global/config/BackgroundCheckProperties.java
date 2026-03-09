package com.internalemployeeportal.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "external.background-check")
public class BackgroundCheckProperties {

    private String baseUrl;
    private String createPath;
    private int connectTimeoutMillis;
    private int readTimeoutMillis;
}