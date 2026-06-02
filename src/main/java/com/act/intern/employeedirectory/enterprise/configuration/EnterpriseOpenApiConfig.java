package com.act.intern.employeedirectory.enterprise.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnterpriseOpenApiConfig {

    @Bean
    public OpenAPI enterpriseOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Employee Directory Enterprise API")
                .description("DDD + Hexagonal Architecture Implementation using CQRS and Clean Architecture")
                .version("2.0.0")
                .contact(new Contact().name("ACT Intern").email("intern@act.com"))
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
