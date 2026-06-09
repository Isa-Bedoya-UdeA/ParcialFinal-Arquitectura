package com.udea.parcialfinal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuración de metadata para Swagger / OpenAPI.
 * La UI queda disponible en /swagger-ui.html y el JSON en /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI parcialFinalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Colegio UdeA - Parcial Final")
                        .description("API REST para la gestión de estudiantes, materias y notas. "
                                + "Versionada por URI: /api/v1/...")
                        .version("v1")
                        .contact(new Contact()
                                .name("Equipo Parcial Final")
                                .email("soporte@udea.example"))
                        .license(new License()
                                .name("Uso académico")
                                .url("https://www.udea.edu.co")));
    }
}
