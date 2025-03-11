package com.bss.bssserverapi.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.domain}")
    private String domain;

    @Bean
    public OpenAPI api() {

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Token", securityScheme()))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Token"))
                .servers(List.of(
                        createServer(domain),
                        createServer("http://localhost:8080")
                ));
    }

    private SecurityScheme securityScheme() {

        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
    }

    private Server createServer(String url) {

        return new Server().url(url);
    }
}
