package com.lvlup.tienda.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API
 * Accesible en: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Nombre del esquema de seguridad
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("LvlUp E-commerce API")
                        .version("V3")
                        .description("API REST para plataforma de e-commerce gaming multiplataforma. " +
                                "Gestiona productos, usuarios, carritos de compra y pedidos con autenticación JWT " +
                                "y control de roles (ADMIN, VENDEDOR, CLIENTE).")
                        .contact(new Contact()
                                .name("Simón Villar y Carlos Muñoz")
                                .email("soporte@lvlupgaming.cl")
                                .url("https://github.com/lvlup-gaming"))
                        .termsOfService("Aplicación de uso pedagógico desarrollada para el ramo Fullstack2")
                        .license(new License()
                                .name("Uso Académico")
                                .url("https://www.duoc.cl")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo Local"),
                        new Server()
                                .url("https://api.lvlupgaming.cl")
                                .description("Servidor de Producción (si aplica)")
                ))
                // Configuración de seguridad JWT
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el token JWT obtenido del endpoint /login")
                        )
                );
    }
}
