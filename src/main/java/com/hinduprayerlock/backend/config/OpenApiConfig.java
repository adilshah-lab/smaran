package com.hinduprayerlock.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI prayerLockOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Prayer Lock API")
                        .description("Backend APIs for Prayer Lock mobile application")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Prayer Lock Team")
                                .email("support@prayerlock.app"))
                        .license(new License()
                                .name("Apache 2.0")))

                // 🔐 Add Security Requirement globally
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))

                // 🔐 Define Security Scheme
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}