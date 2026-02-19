package com.hinduprayerlock.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI prayerLockOpenAPI() {
        return new OpenAPI()
//                .servers(List.of(
//                        new Server().url("https://smaraan.com")
//                ))
                .info(new Info()
                        .title("Prayer Lock API")
                        .description("Backend APIs for Prayer Lock mobile application")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Prayer Lock Team")
                                .email("support@prayerlock.app"))
                        .license(new License()
                                .name("Apache 2.0")));
    }
}
