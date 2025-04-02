package com.basicAuthOAuth2.basicAuthOAuth2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration

public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                new Info().title("Basic-Auth OAuth2")
                        .description("Basic-Auth OAuth2 API by Mr.Aayush Ghising"))
                .servers(Arrays.asList(
                   new Server().url("http://localhost:8080").description("localhost"),
                   new Server().url("http://localhost:8081").description("live")
                ));

    }
}
