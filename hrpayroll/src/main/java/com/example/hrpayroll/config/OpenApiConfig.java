package com.example.hrpayroll.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("HR/Payroll API")
                        .description("Endpoints for departments, employees, payroll runs & items")
                        .version("v1")
                        .contact(new Contact().name("HR Payroll").email("support@example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repository")
                        .url("https://example.com/repo"));
    }
}
