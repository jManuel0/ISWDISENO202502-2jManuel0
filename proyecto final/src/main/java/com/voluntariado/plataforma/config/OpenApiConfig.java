package com.voluntariado.plataforma.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT")))
                .info(new Info()
                        .title("API Plataforma de Voluntariado ONG")
                        .version("1.0.0")
                        .description("""
                                API REST para la gestión de voluntariado en ONGs.

                                ## Funcionalidades principales:
                                - Registro y autenticación de voluntarios
                                - Gestión de actividades de voluntariado
                                - Inscripción y seguimiento de participación
                                - Generación de certificados
                                - Sistema de notificaciones
                                - Mensajería interna

                                ## Roles:
                                - **VOLUNTARIO**: Usuario básico que puede inscribirse a actividades
                                - **COORDINADOR**: Puede crear y gestionar actividades
                                - **ADMINISTRADOR**: Acceso total al sistema
                                """)
                        .contact(new Contact()
                                .name("Soporte ONG")
                                .email("soporte@ong.org"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
