package com.lvlup.tienda.security;

import com.lvlup.tienda.security.filter.JwtAuthenticationFilter;
import com.lvlup.tienda.security.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // <--- IMPORTANTE
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // <--- 1. HABILITAMOS ANOTACIONES
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests((authz) -> {
        authz
            // --- RUTAS PÚBLICAS ---
            // Registro y Login
            .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
            .requestMatchers(HttpMethod.POST, "/login").permitAll()
            // Documentación Swagger/OpenAPI
            .requestMatchers("/", "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
            
            // NUEVO: Productos públicos (Ver lista y detalle)
            .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()

            // --- RUTAS PROTEGIDAS (PRODUCTOS) ---
            // Permitir que usuarios autenticados actualicen stock (para checkout)
            .requestMatchers(HttpMethod.PUT, "/api/v1/products/{id}").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/v1/products").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/products/{id}").hasRole("ADMIN")

            // --- RUTAS PROTEGIDAS (USUARIOS) ---
            // IMPORTANTE: Recuperamos estas líneas para proteger los datos de usuarios
            .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN") // Solo Admin ve la lista
            .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").hasAnyRole("ADMIN", "VENDEDOR", "CLIENTE") // Ver perfil propio
            
            // Acciones de escritura sobre usuarios
            .requestMatchers(HttpMethod.POST, "/api/v1/users").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}").hasAnyRole("ADMIN", "CLIENTE")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}").hasRole("ADMIN")

            // --- RUTAS PROTEGIDAS (CARRITOS) ---
            // Todos los usuarios autenticados pueden acceder a su propio carrito
            .requestMatchers("/api/v1/carts/**").hasAnyRole("ADMIN", "VENDEDOR", "CLIENTE")

            // --- REGLAS GLOBALES ---
            .anyRequest().authenticated();
    })
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}