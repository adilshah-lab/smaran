package com.hinduprayerlock.backend.config;

import com.hinduprayerlock.backend.utils.JwtAuthFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class OpenApiConfig {

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
                                .name("Apache 2.0")));
    }

}
