package com.reactivehub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reactiveProductHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reactive Product Hub API")
                        .version("1.0.0")
                        .description(
                                "API reactiva para gestionar productos "
                                + "e integrar información de proveedores externos.")
                        .contact(new Contact()
                                .name("Reactive Product Hub")));
    }
}
