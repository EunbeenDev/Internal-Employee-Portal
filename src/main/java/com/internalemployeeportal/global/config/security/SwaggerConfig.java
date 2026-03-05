package com.internalemployeeportal.global.config.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI noaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Internal Employee Portal API")
                        .description("사내 직원 정보 관리 시스템 API 문서")
                        .version("v1"));
    }
}
