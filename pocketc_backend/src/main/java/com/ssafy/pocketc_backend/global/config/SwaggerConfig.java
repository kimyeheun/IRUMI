package com.ssafy.pocketc_backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Irumi API 명세서", version = "v1"))
public class SwaggerConfig {

    @Bean
    GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
            .group("전체 API")
            .pathsToMatch("/api/v1/**")
            .build();
    }
}