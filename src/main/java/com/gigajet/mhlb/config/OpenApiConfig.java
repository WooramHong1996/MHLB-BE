package com.gigajet.mhlb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String springdocVersion) {
        Info info = new Info()
                .title("Pin me API Document")
                .version(springdocVersion)
                .description("항해99 실전 프로젝트 API 명세서");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

}
